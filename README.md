# KATE
**K**ubernetes **A**ssistant for multi-**T**enant **E**nvironments

KATE is an Anything as a Service (XaaS) tool, it is used to deploy Kubernetes native resources as well as Custom resources. 

## Components `kate.degree9.io/v1`

### Tenants
A Tenant represents the desired state of a collection of services. 
```json
{
  "kind": "Tenant",
  "apiVersion": "kate.degree9.io/v1",
  "metadata": {
    "name": "example"
  },
  "spec": {}
}
```

- [ ] Create a Tenant and Services

> These are backed as Custom Resources within Kubernetes.

### Services
A Service represents the desired state of a single service.
```json
{
  "kind": "Service",
  "apiVersion": "kate.degree9.io/v1",
  "metadata": {
    "name": "example",
    "path": "/example",
    ""
  },
  "spec": {}
}
```

- [ ] Create a Service

> These are backed as Custom Resources within Kubernetes.

### Kubernetes Resources
A Kubernetes Resource is implemented as a Resource Definition + FeathersJS Service.
These interact with the Kubernetes Cluster using the JavaScript client.
- [ ] Namespace
  - [ ] Create a Namespace
- [ ] Deployment
  - [ ] Create a Deployment

### Mailgun Resource
TODO: A FeathersJS Service to interact with the Mailgun API.

### Webhook Resource
TODO: A FeathersJS Service to invoke a webook from an external API.
