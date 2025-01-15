# MicroTX Sample Project

## Overview

This repository contains sample implementations for demonstrating and testing MicroTX, a distributed transaction management solution. It includes examples both with and without Teller Spring, providing a comprehensive toolkit for developers to understand and implement distributed transactions in various scenarios.

## Contents

1. **Teller Spring Sample**: Demonstrates the integration of MicroTX with Teller Spring, showcasing how to manage distributed transactions in a Spring&Helidon-based environment.

2. **NON XA Sample**: A standalone example that illustrates the use of MicroTX AND NON-XA transactions.


https://github.com/junoyoon/fastcampus-jenkins.git


## Purpose

The primary goals of this repository are:

- To provide practical examples of MicroTX implementation
- To demonstrate the flexibility of MicroTX in different architectural setups
- To serve as a learning resource for developers working with distributed transactions

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Maven 3.6.x or higher
- Docker (for running containerized databases, if applicable)

### Setup and Running

1. Clone the repository:

command :

    git clone https://github.com/kubelin/microtx-mysample-01.git
    
2. Navigate to the project directory:

command : 

    cd microtx-mysample-01

3. Build the project:

command :

    mvn clean package

4. Run the desired sample (refer to individual sample READMEs for specific instructions)

## Sample Descriptions

### Teller Spring Sample

Located in `teller-spring/`, this example demonstrates:
- Integration of MicroTX with Spring Framework
- Management of distributed transactions across multiple services

### Custom NON-XA Sample

Found in `my-nonxa-department-spring/` and `my-nonxa-teller-spring/`, this sample covers:
- Direct implementation of NONXA transactions

## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please review our contribution guide
( following oracle-microtx )

## License

Copyright (c) 2023 Oracle and/or its affiliates.
Released under the Universal Permissive License v1.0 as shown at https://oss.oracle.com/licenses/upl/.
( following oracle-microtx )

## Acknowledgments

For more information on MicroTX, please visit [https://github.com/oracle-samples/microtx-samples).
