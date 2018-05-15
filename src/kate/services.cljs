(ns kate.services
  (:refer-clojure :exclude [namespace])
  (:require
    [goog.object :as obj]
    [meta.server :as svr]
    [meta.services :as svc]
    [meta.promise :as prom]
    ;[degree9.mongodb :as mongodb]
    [degree9.hooks :as hooks]
    [degree9.kubernetes :as k8s]))

;; Kate Hooks ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn hook-set! [hook index val]
  (obj/set hook index (clj->js val)))

(defn hook-get [hook index]
  (js->clj (obj/get hook index)))

(defn- hook-params [hook]
  (hook-get hook "params"))

(defn- hook-params! [hook data]
  (hook-set! hook "params" data))

(defn- hook-data [hook]
  (hook-get hook "data"))

(defn- hook-data! [hook data]
  (hook-set! hook "data" data))

(defn hook-merge [& hooks]
  (let [deep-merge (partial merge-with into)]
    (apply deep-merge hooks)))

(defn- move-in [data source dest]
  (if-let [val (get data source)]
    (assoc-in (dissoc data source) dest val)
    data))

(defn move-data [source dest]
  (fn [hook]
    (doto hook
      (hook-data! (move-in (hook-data hook) source dest)))))

(defn- copy-in [data source dest]
  (if-let [val (get-in data source)]
    (assoc-in data dest val)
    data))

(defn copy-data [source dest]
  (fn [hook]
    (doto hook
      (hook-data! (copy-in (hook-data hook) source dest)))))

(defn data->query
  "Shift an index from hook.data to hook.params.query"
  [index]
  (fn [hook]
    (let [data   (hook-data hook)
          params (hook-params hook)
          val    (get data index)]
        (doto hook
          (hook-data! (dissoc data index))
          (hook-params! (assoc-in params ["query" index] val))))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kate Service Invoker ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn method-factory
  "Returns a collection of service method calls."
  [app method data params]
  (let [service (partial svc/service app)]
    (mapv (fn [[k v]] #(method (service k) v params)) data)))

(defn service-invoker
  "Invoke consecutive services where each key/value pair is a service path/data pair."
  [method]
  (fn [hook]
    (let [hook   (js->clj hook)
          app    (get hook "app")
          data   (get-in hook ["data" "spec"])
          params (get hook "params")
          proms  (method-factory app method data params)]
      (-> (prom/promise)
        (prom/serial proms)
        (prom/log)
        (prom/err)))))

(defn service-creator []
  (service-invoker svc/create))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kate Service Plans ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; TODO: Implement Service Plans
; Service plans provide a unit of billing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kate Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO: Kate currently requires tenant data to match a hard coded services
;; a needed improvement is the translation to stored services, stored services
;; should provide abstraction over the coded endpoints, and include defaults and
;; overrides to service endpoints.
(defn services [app]
  (svr/api app "/services"
    (k8s/custom-resource {:group "kate.degree9.io" :kind "Service"})
    {}))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kate Tenant ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn tenant [app]
  (svr/api app "/tenant"
    (k8s/custom-resource {:group "kate.degree9.io" :kind "Tenant"})
    {:before {:create [(move-data "name" ["metadata" "name"])]}
     :after  {:create [(service-creator)]}}))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kubernetes Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; create k8s pods
; create k8s jobs
(defn namespace [app]
  (svr/api app "/kubernetes/namespace" (k8s/namespace)
    {:before {:all    [(hooks/block-external)]
              :create [(move-data "name" ["metadata" "name"])]}}))

(defn deployment [app]
  (svr/api app "/kubernetes/deployment" (k8s/deployment)
    {:before {:all    [(hooks/block-external)]
              :create [(data->query "namespace")
                       (move-data "name" ["metadata" "name"])
                       (move-data "labels" ["metadata" "labels"])
                       (move-data "selector" ["spec" "selector" "matchLabels"])
                       (copy-data ["spec" "selector" "matchLabels"]
                                  ["spec" "template" "metadata" "labels"])
                       (move-data "containers" ["spec" "template" "spec" "containers"])]}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Mailgun Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; send email(s)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; MongoDB Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; create cluster?
; create database
; create collection
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Webhook Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; invoke Webhook
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
