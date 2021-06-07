# Helm in local environment
>The build will output docker image and helm chart. This sharing is meant to share a piece info around helm and how to deal with that during day to day development works

`tips`

After quite sometimes, building same docker images over and over again, there are a lot of images in local registry without tag
Do clean up with,
```shell
docker rm $(docker images --filter "dangling=true" -q --no-trunc) --force
```
Use docker comes from minikube to build the docker image so that, when pod pulls the docker image from the cluster, it is able to reach,
To enable this, instead of having a separate docker to build, use the one from minikube,
```shell
eval $(minikube docker-env)
```
See here, [https://stackoverflow.com/questions/42564058/how-to-use-local-docker-images-with-minikube]
## How
`Helm installation`
[Helm Installation](https://helm.sh/docs/intro/install/)

`Commont Usage`

* More convenient way to install helm package
`upgrade --install`

Install if it does not exist and upgrade if it exists  

Accept 3 different format for the package input,

```shell
helm upgrade --install $release_name package_dir
helm upgrade --install $release_name package.tgz
helm upgrade --install $release_name $repo_name/package
```

`--dry-run` option

To see the installation flow, will not do the actuall installation. It will detect some problem,
like resource conflict, etc

```shell
helm upgrade --install $release_name package_dir --dry-run --debug
```
Once the `helm install` command issued, the command will return immediately
But the deployment may not ready yet. It is just scheduling the kubernetes object, deployment, pod to be created.
normally need to monitor the pod/deployment status before it can serve the request.

`beyond hell`
Filter pods in the list
```shell
kubectl get pods -l environment=production,tier=frontend
```

Check the selector
```shell
kubectl describe pod minimart-db464b68c-wpv8c

Name:         minimart-db464b68c-wpv8c
Namespace:    default
Priority:     0
Node:         minikube/192.168.64.4
Start Time:   Fri, 21 May 2021 15:45:01 +0800
Labels:       app.kubernetes.io/instance=minimart
              app.kubernetes.io/name=minimart
              pod-template-hash=db464b68c

```
try
```shell
kubectl get pods -l app.kubernetes.io/instance=minimart
```

```shell
kubectl get pods -l app.kubernetes.io/instance=minimart

NAME                       READY   STATUS    RESTARTS   AGE
minimart-db464b68c-wpv8c   1/1     Running   0          6m20s

```


## Backend

`Containerization`

It is built, `samutup/minimart:0.1`

```file
│   Dockerfile
│
├───minimart
│       minimart.yaml
│
├───bin
│       start_minimart.sh
│
└───jars
        minimart.jar

```


`Helm Chart` package manager

```file
│   values-dev.yaml
│
└───chart
    │   Chart.yaml
    │   README.md
    │   values.yaml
    │
    ├───configs
    │       minimart.yaml
    │
    └───templates
            configmaps.yaml
            deployment.yaml
            NOTES.txt
            service.yaml
            _helpers.tpl

```

`Binary`

Based on `gradle` build tool

Framework: `vert-x`

# Demo
* Show case functionality
* Show case helm install, upgrade, change parameter and update config

Assuming that helm chart delivery has this structure

```file
dev/helm-atm-poc
│   values-dev.yaml
│
└───chart
    │   Chart.yaml
    │   README.md
    │   values.yaml
    │
    ├───configs
    │       atm.yaml
    │
    └───templates
            configmaps.yaml
            deployment.yaml
            NOTES.txt
            service.yaml
            _helpers.tpl

```

```bash
cd dev
helm upgrade --install minimart minimart/chart/ -f minimart/values-dev.yaml
```
>If the helm release with name `minimart` exists, the helm will update it and increase the release version. If not it will install the new release.
`values.yaml` is the default values to be fed to the charts then propagated to the kubernetes resources such as `deployment`, `ConfigMaps` , `Service` and `Pod`. In this case, the `values.yaml` hold the values to be reflected in both application layer (or `docker container`) and kubernetes layer (eg. `services`)
Example for the value that is managed by the values.yaml, `listening http port` for the application which is used in the `atm-poct` docker container. While the `NodePort` exposed into external which is managed by `Service` is also configurable.

```yaml
dev: true
service:
  type: NodePort
  ports:
    http_port: 30841
    debug_port: 30842
deployment:
  replicas: 1
  strategy: {}
  affinity: {}
  nodeSelector: {}
  tolerations: []
  resources:
    limits:
      memory: 512Mi
    requests:
      memory: 100Mi
      cpu: 100m
apps:
  httpport: 8002
  rest:
    stat: "/minimart-stat"
    bev: "/minimart-bev"
image: samutup/minimart:0.1
```

`Usage of _helpers.tpl`

>is a template engine that read the `values` and sourced it as reusable `template` to be used across different `definition` file. Example,

`define template` in `_helpers.tpl`
```shell
{{/*
Return the http_port where the server is running
*/}}
{{- define "{{ .Chart.Name }}.port" -}}
{{- if hasKey .Values.apps "httpport" -}}
{{- printf "%g" .Values.apps.httpport | trunc 63 |trimSuffix "-" -}}
{{- else -}}
{{- printf "80" -}}
{{- end -}}
```
In this example, a template named `{{ .Chart.Name }}.port"` is defined to read the listening port passed through `values` `apps.httpport`

```shell
apps:
  httpport: 8002
```
It is used to override the `minimart.yaml`,
```shell
server:
    httpport: {{ include "{{ .Chart.Name }}.port" . }}
```

# Coming Up
The CI/CD pipeline will need to fetch helm chart from repository and install it.
So the individual micro-services project needs to produce the helm chart and push it into the repository.
It means, at the individual microservices project will need to include the `helm create` then `helm push` in its build life cycle


# AIRLab helm script
Push 
```shell
helm push minimart/chart chartmuseum

```
