(ns app.services
  (:refer-clojure :exclude [namespace])
  (:require
    [meta.server :as svr]
    [degree9.kubernetes :as k8s]))


(defn namespace [app]
  (svr/api app "/namespace" (k8s/namespace) {}))

(defn deployment [app]
  (svr/api app "/:namespace/deployment" (k8s/deployment) {}))
