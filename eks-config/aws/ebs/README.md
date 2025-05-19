# Install
```bash
helm upgrade -i aws-ebs-csi-driver -n kube-system aws-ebs-csi-driver/aws-ebs-csi-driver \
--set image.repository=602401143452.dkr.ecr.ap-northeast-2.amazonaws.com/eks/aws-ebs-csi-driver \
--set controller.serviceAccount.create=false \
--set controller.serviceAccount.name=ebs-csi-controller-sa
```