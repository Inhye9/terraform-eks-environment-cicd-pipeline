region          = "ap-northeast-2"

profile         = "<profile>"

project_name    = "<project_name>"

env             = "test"   

eks_version     = "1.31"

vpc_id          = "<vpc_id>"  

//eks_additional_sg_ids  = []

//eks_node_sg_ids        = []

eks_addon_versions = {
  vpc-cni                = "v1.19.2-eksbuild.1"
  eks-pod-identity-agent = "v1.3.4-eksbuild.1"
  kube-proxy             = "v1.31.2-eksbuild.3"
  metrics-server         = "v0.7.2-eksbuild.1"
  coredns                = "v1.11.3-eksbuild.1"
  aws-ebs-csi-driver     = "v1.38.1-eksbuild.1"
  aws-efs-csi-driver     = "v2.1.3-eksbuild.1"
  aws-lb-controller      = "1.10.1"
  cluster-autoscaler     = "9.44.0"
}

eks_node_name_info = ["app", "mgmt"]


eks_nodegroup_info = {
  app = {
    image_id      = "ami-00e57c87948c0ea68"   //amazon-eks-node-1.31-v20250317 
    //instance_type = "t2.micro"
    instance_type = "t3.medium"
    disk_capacity = 20
    node_capacity = [1, 1, 2]
    userdata      = ["userdata/pre_userdata.sh", ""]
  }

  mgmt = {
    image_id      = "ami-00e57c87948c0ea68"   //amazon-eks-node-1.31-v20250317
    //instance_type = "t2.micro"     //max ENI 2개 * IPv4 2개 (1개 ENI당)    = 총 4개 IPAM
    instance_type = "t3.medium"      //max ENI 3개 * IPv4 6개 (1개 ENI당) -1 = 총 16개 IPAM
    disk_capacity = 20
    node_capacity = [1, 1, 2]
    userdata      = ["userdata/pre_userdata.sh", ""]
  }
}


lt_keypair_name = "2244615-keypair"

//workbench_sg_id = "sg-0123456789abcdef3"

lt_resource_tags = ["instance", "volume", "network-interface"]

//jenkins_ec2_arn = ""

