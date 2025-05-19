provider "aws" {
  region = var.region
}


# ------------------------------------------------------------------------
# Tag
# ------------------------------------------------------------------------
locals {
  common_tags = {
    CreatedBy = "Terraform"
    Group     = "${var.instance_name}-group"
  }
}


# EC2 인스턴스 생성
resource "aws_instance" "ec2" {
  ami                     = data.aws_ami.amazon_linux.id
  instance_type           = var.instance_type
  key_name                = var.key_pair_name
  subnet_id               = var.subnet_id
  #security_groups_ids = [aws_security_group.jenkins_sg.id]
  vpc_security_group_ids  = [aws_security_group.sg.id]
  iam_instance_profile    = var.instance_profile_role

  user_data = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install docker -y

              # 4. docker-compose 설치
              sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/bin/docker-compose
              sudo chmod +x /usr/bin/docker-compose
              sudo docker-compose -v

              ## Start Docker service
              sudo systemctl start docker
              sudo systemctl enable docker
              EOF

  # public ip 할당 허용
  associate_public_ip_address = true

  tags = merge(local.common_tags,{
    Name = "${var.instance_name}"
  })
}

data "aws_ami" "amazon_linux" {
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "owner-id"
    values = ["137112412989"] # Amazon
  }
}

output instance_id {
  value = aws_instance.ec2.id
}