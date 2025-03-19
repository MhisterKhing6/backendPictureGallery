# Picture Gallery Application

## Overview
The Picture Gallery Application is a cloud-native image storage and sharing platform deployed using AWS infrastructure. This project leverages AWS services such as VPC, ECS, Fargate, ALB, S3, ECR, IAM, and CloudWatch for a secure, scalable, and efficient deployment.

## Architecture
The infrastructure is defined using AWS CloudFormation templates and consists of:
- **VPC & Networking:** A custom VPC with public and private subnets, internet gateway, and NAT gateway.
- **Security:** IAM roles, security groups, and VPC endpoints to restrict access to required AWS services.
- **Storage:** An S3 bucket for storing images securely.
- **Compute:** An ECS cluster running Fargate tasks to host the application.
- **Containerization:** The application is packaged as a Docker container and stored in Amazon ECR.
- **Load Balancing:** An ALB for handling incoming traffic and distributing it across ECS tasks.
- **Logging & Monitoring:** CloudWatch logs for application monitoring.

## AWS Services Used
- **Amazon VPC**: Defines the network environment with public and private subnets.
- **Amazon S3**: Stores user-uploaded images.
- **Amazon ECR**: Hosts the Docker container image for the application.
- **Amazon ECS (Fargate)**: Runs the containerized application.
- **Application Load Balancer (ALB)**: Handles HTTP traffic and routes requests.
- **Amazon IAM**: Manages access control for ECS tasks and AWS resources.
- **Amazon CloudWatch**: Collects and monitors logs and metrics.

## Deployment Steps
### Prerequisites
- AWS CLI installed and configured.
- Docker installed.
- AWS CloudFormation permissions.

### Steps
1. **Create the Infrastructure**
   ```sh
   aws cloudformation deploy --template-file infrastructure.yaml --stack-name PictureGalleryInfra
   ```

2. **Build and Push Docker Image**
   ```sh
   docker build -t my-picture-gallery .
   aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com
   docker tag my-picture-gallery:latest <account-id>.dkr.ecr.<region>.amazonaws.com/my-picture-gallery-repository:latest
   docker push <account-id>.dkr.ecr.<region>.amazonaws.com/my-picture-gallery-repository:latest
   ```

3. **Deploy the ECS Service**
   ```sh
   aws cloudformation deploy --template-file ecs.yaml --stack-name PictureGalleryECS --parameter-overrides ECRRepositoryUri=<account-id>.dkr.ecr.<region>.amazonaws.com/my-picture-gallery-repository:latest
   ```

## Outputs
Once deployed, retrieve the ALB DNS name:
```sh
aws cloudformation describe-stacks --stack-name PictureGalleryECS --query "Stacks[0].Outputs[?OutputKey=='ALBDNSName'].OutputValue" --output text
```
Access the application using the ALB DNS name in your browser.

## Conclusion
This Picture Gallery Application provides a scalable and secure platform for storing and sharing images, leveraging AWS best practices and managed services.

