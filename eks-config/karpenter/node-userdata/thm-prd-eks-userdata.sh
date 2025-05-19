#!/bin/bash
useradd appadmin

rm -rf /usr/local/aws
rm /usr/local/bin/aws
rm /usr/bin/aws
rm /usr/bin/aws_completer
rm -rf /usr/local/aws-cli

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install -i /usr/local/aws-cli -b /usr/bin

#deepsecurity
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/deepsecurity/AgentDeploymentScript.sh /tmp
chmod 755 /tmp/AgentDeploymentScript.sh
./tmp/AgentDeploymentScript.sh
rm -rf /tmp/AgentDeploymentScript.sh

#eks
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/sudoers /etc/sudoers
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/sshd_config /etc/ssh/sshd_config
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/pwquality.conf /etc/security/pwquality.conf
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/system-auth /etc/pam.d/system-auth
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/motd /var/lib/update-motd/motd
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/30-banner /etc/update-motd.d/30-banner
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/profile /etc/profile
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/su /etc/pam.d/su
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/login.defs /etc/login.defs
aws s3 cp s3://aim-prd-pri-eks-userdata-s3/data/sshd /etc/pam.d/sshd
ln -s /var/lib/update-motd/motd /etc/motd

chmod 440 /etc/sudoers
chmod 600 /etc/ssh/sshd_config
chmod 644 /etc/security/pwquality.conf
chmod 644 /etc/pam.d/system-auth
chmod 644 /etc/profile
chmod 644 /etc/pam.d/su
chmod 644 /etc/login.defs
chmod 755 /etc/update-motd.d/30-banner
chmod 640 /etc/at.deny
chmod 600 /etc/hosts
chmod 644 /etc/pam.d/sshd
chown root:root /etc/hosts

chmod 750 /usr/bin/crontab
chmod 640 /etc/crontab
chmod 640 /etc/cron.hourly
chmod 640 /etc/cron.daily
chmod 640 /etc/cron.weekly
chmod 640 /etc/cron.monthly
chmod 640 /etc/cron.deny
chmod 640 /var/spool/cron/

timedatectl set-timezone Asia/Seoul

source /etc/profile

systemctl restart sshd

chmod -s /usr/bin/newgrp
chmod -s /sbin/unix_chkpwd
chmod -s /usr/bin/at

echo `date`
