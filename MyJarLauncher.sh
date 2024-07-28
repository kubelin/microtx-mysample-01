#!/bin/bash

# 실행 옵션을 표시하고 선택하는 함수
show_menu() {
    echo "대상 입력 >>"
    echo "1. Spring Boot JAR 기본 실행"
    echo "2. Spring Boot JAR 새로운 설정으로 실행"
    echo "3. Spring Boot JAR 테스트 모드 실행"
    echo "4. 이전 실행 기록(최대 10개)"
    echo "5. 포트 설정 후 실행"
}

# JAR 파일 선택 함수
select_jar_file() {
    echo "실행할 JAR 파일을 선택하세요:"    
    local files=($(find . -type f \( -path "*/build/libs/*.jar" -o -path "*/target/*.jar" \)))
    if [ ${#files[@]} -eq 0 ]; then
        echo "실행할 JAR 파일을 찾을 수 없습니다."
        exit 1
    fi
    for i in "${!files[@]}"; do
        echo "$((i+1)). ${files[i]}"
    done

    read -p "파일 번호를 선택하세요: " file_choice
    if [[ $file_choice -gt 0 && $file_choice -le ${#files[@]} ]]; then
        selected_file=${files[$((file_choice-1))]}
        echo "선택한 파일: $selected_file"
    else
        echo "잘못된 선택입니다. 다시 시도해주세요."
        select_jar_file
    fi
}

# 이전 실행 기록을 보여주는 함수
show_execution_history() {
    if [ -f execution_history.txt ]; then
        echo "이전 실행 기록:"
        tail -n 10 execution_history.txt
    else
        echo "실행 기록이 없습니다."
    fi
}

# Spring Boot JAR 파일을 실행하고 기록하는 함수
execute_jar() {
    local command=$1
    local log_dir=$2
    local port=$3
    local run_in_background=$4  # 백그라운드 실행 여부

    echo "실행 중: $command"
    echo "로그 디렉토리: $log_dir"
    mkdir -p $log_dir

    # IP 주소 가져오기
    local ip=$(hostname -I | awk '{print $1}')

    # 날짜 형식 지정
    local date=$(date +%Y%m%d_%H%M%S)

    # 로그 파일 이름 생성
    local log_file="${ip}_${port}_${date}_nohup.log"

    echo "실행 기록에 저장..."
    echo $(date): $command >> execution_history.txt

    if [ "$run_in_background" = true ]; then
        # 백그라운드에서 실행
        nohup sh -c "$command 2>&1 | tee -a \"$log_dir/$log_file\"" &
        echo "프로세스가 백그라운드에서 실행 중입니다."
        echo "로그를 보려면 다음 명령어를 사용하세요: tail -f \"$log_dir/$log_file\""
    else
        # 포그라운드에서 실행
        $command 2>&1 | tee -a "$log_dir/$log_file"
    fi 
}

# 로그 디렉토리를 생성하는 함수
create_log_dir() {
    local jar_name=$(basename $selected_file .jar)
    local date_dir=$(date +%Y-%m-%d)
    local log_dir="./logs/$date_dir/$jar_name"

    mkdir -p $log_dir
    echo $log_dir
}

# 메뉴를 표시하고 사용자로부터 선택을 받음
while true; do
    select_jar_file
    show_menu
    read -p "옵션을 선택하세요 (1-5): " choice

    log_dir=$(create_log_dir)

    
# 메인 스크립트의 case 문
case $choice in
    1)
        port=$(echo $selected_file | grep -oP '(?<=--server.port=)\d+' || echo "8080")
        execute_jar "java -jar $selected_file" $log_dir $port false
        ;;
    2)
        read -p "새로운 설정을 입력하세요: " new_settings
        port=$(echo $new_settings | grep -oP '(?<=--server.port=)\d+' || echo "8080")
        execute_jar "java -jar $selected_file $new_settings" $log_dir $port false
        ;;
    3)
        port=$(echo $selected_file | grep -oP '(?<=--server.port=)\d+' || echo "8080")
        execute_jar "java -jar $selected_file --spring.profiles.active=test" $log_dir $port false
        ;;
    4)
        show_execution_history
        ;;
    5)
        read -p "사용할 포트를 입력하세요: " port
        read -p "백그라운드에서 실행하시겠습니까? (y/n): " bg_choice
        if [[ $bg_choice == "y" ]]; then
            execute_jar "java -jar $selected_file --server.port=$port" $log_dir $port true
        else
            execute_jar "java -jar $selected_file --server.port=$port" $log_dir $port false
        fi
        ;;
    *)
        echo "잘못된 선택입니다. 다시 시도해주세요."
        ;;
esac
done  # end of shell