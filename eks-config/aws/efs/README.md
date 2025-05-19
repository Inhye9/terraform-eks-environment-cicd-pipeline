# Install
```bash
helm upgrade -i aws-efs-csi-driver -n kube-system aws-efs-csi-driver/aws-efs-csi-driver \
--set image.repository=602401143452.dkr.ecr.ap-northeast-2.amazonaws.com/eks/aws-ebs-csi-driver \
--set controller.serviceAccount.create=false \
--set controller.serviceAccount.name=ebs-csi-controller-sa
```