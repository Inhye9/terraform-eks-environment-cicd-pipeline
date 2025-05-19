# Install
* Must same namespace of alb
### DEV 
```bash
helm upgrade -i aws-load-balancer-controller -n kube-system eks/aws-load-balancer-controller \
--set clusterName=aim-dev-eks-cluster \
--set serviceAccount.create=false \
--set vpcId=vpc-0c0658623baf055bf \
--set region=ap-northeast-2 \
--set serviceAccount.name=aws-load-balancer-controller
```

### STG
```bash
helm upgrade -i aws-load-balancer-controller -n kube-system eks/aws-load-balancer-controller \
--set clusterName=aim-stg-eks-cluster \
--set serviceAccount.create=false \
--set vpcId=vpc-0c0658623baf055bf \
--set region=ap-northeast-2 \
--set serviceAccount.name=aws-load-balancer-controller
```

### QA
```bash
helm upgrade -i aws-load-balancer-controller -n kube-system eks/aws-load-balancer-controller \
--set clusterName=aim-qa-eks-cluster \
--set serviceAccount.create=false \
--set vpcId=vpc-0c0658623baf055bf \
--set region=ap-northeast-2 \
--set serviceAccount.name=aws-load-balancer-controller
```

### PRD
```bash
helm upgrade -i aws-load-balancer-controller -n kube-system eks/aws-load-balancer-controller \
--set clusterName=aim-prd-eks-cluster \
--set serviceAccount.create=false \
--set vpcId=vpc-0c0658623baf055bf \
--set region=ap-northeast-2 \
--set serviceAccount.name=aws-load-balancer-controller
```

# Delete

```bash
helm delete aws-load-balancer-controller -n kube-system
```