# KATE
**K**ubernetes **A**ssistant for multi-**T**enant **E**nvironments

KATE is an Anything as a Service (XaaS) tool, it is used to deploy Kubernetes native resources as well as Custom resources. 

## Core Components

### Tenants
A Tenant represent the desired state of a collection of services. 

- [ ] Create a Tenant and Services

> These are backed as Custom Resources within Kubernetes.

### Services
A Service represents the desired state of a single service.

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
