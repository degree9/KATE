(ns degree9.kubernetes
  (:refer-clojure :exclude [namespace])
  (:require
    [cljs.nodejs :as node]
    [goog.object :as obj]
    [feathers.errors :as error]))

;; Kubernetes API ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def k8s (node/require "@kubernetes/client-node"))

(def api (k8s.Config.defaultClient))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kubernetes Helpers ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- k8s->clj
  "Converts Kubernetes response to ClojureScript."
  [k8s]
  (js->clj k8s :keywordize-keys true))

(defn- k8s-response [res]
  (clj->js (:body (k8s->clj res))))

(defn- k8s-error
  "Format a Kubernetes error as a [message details] pair."
  [err]
  (let [{:keys [body]} (k8s->clj err)
        message (:message body)
        details (:details body)]
    [message (clj->js details)]))

(defn- not-found
  "Emits a NotFound error from a Kubernetes error response."
  [err]
  (let [[message details] (k8s-error err)]
    (error/not-found message details)))

(defn- already-exists
  "Emits a Conflict error from a Kubernetes error response."
  [err]
  (let [[message details] (k8s-error err)]
    (error/conflict message details)))

(defn- create-namespace
  "Create a Kubernetes namespace."
  [data]
  (->
    (.createNamespace api data)
    (.then k8s-response)
    (.catch already-exists)))

(defn- read-namespace
  "Read a Kubernetes namespace."
  [id]
  (->
    (.readNamespace api id)
    (.then k8s-response)
    (.catch not-found)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kubernetes Services ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn namespace [& [opts]]
  (let []
    (reify
      Object
      ;(find [this params]
      ;  ())
      (get [this id & [params]]
        (read-namespace id))
      (create [this data & [params]]
        (create-namespace data)))))
      ;(remove [this id params]
      ;  ()))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
