# disbursement-service
Code challenge of the sequra company

## Table of Contents

- [Project Description](#project-description)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Project Description

The disbursement-service is a project that automates the process of generating disbursements to merchants. It provides a streamlined way to calculate and distribute payments to merchants based on predefined criteria.

## Installation

To install the disbursement-service, follow these steps:
1. Install mongodb for your operating system or use docker to create a container with mongodb. **important**: the configuration of mongodb is set without user and password, if you choose to create a user and password you need to set on the application.yaml these credentials.
2. Clone the repository: git clone https://github.com/julio-mattos/disbursement-service.git
3. Navigate to the project directory: **cd disbursement-service**
4. run this command:
```shell
mvn clean install
```
5. Navigate to the folder **database-importer** and run:
```shell
pip install pymongo
```
6. to import the orders and merchants to database run:
````shell
python main.py
````
7. Navigate back to disbursement-service with **cd ..**
8. run:
````shell
mvn spring-boot:run
````
## Usage

this is a job that start every day accordingly with the configuration of cron in application.yaml, this will start automatically in the time configured.
## Contributing

Contributions to the disbursement-service are welcome! If you would like to contribute, please follow these guidelines:

1. Fork the repository.
2. Create a new branch for your feature or bug fix: git checkout -b feature-name.
3. Make your changes and test thoroughly.
4. Commit your changes: git commit -m "Add feature description".
5. Push to your branch: git push origin feature-name.
6. Submit a pull request detailing your changes.
7. Please ensure that your code adheres to the project's coding style and includes appropriate tests.

## License

The disbursement-service is licensed under the [MIT License](https://joinup.ec.europa.eu/licence/mit-license). You are free to modify and distribute the code under the terms specified in the license.

---
