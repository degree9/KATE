;; Powered by Meta ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; dependency
(set-env! :dependencies '[[degree9/meta "0.3.0-SNAPSHOT"]]
          :checkouts '[[degree9/meta "0.3.0-SNAPSHOT"]
                       [degree9/enterprise "0.0.0-SNAPSHOT"]])
;; require
(require '[meta.boot :as m])

;; init
(m/initialize)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Project ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(deftask develop
  "Start local development."
  []
  (comp
    (m/project :develop true)))
    ;(serve :script "watcher")))

(deftask build
  "Start production build."
  []
  (m/project :build true))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
