(ns fakesso.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [ring.util.codec :as codec]
            [environ.core :refer [env]]))
;; utils

(defn make-query-string [m & [encoding]]
  (let [s #(if (instance? clojure.lang.Named %) (name %) %)
        enc (or encoding "UTF-8")]
    (->> (for [[k v] m]
           (str (codec/url-encode (s k) enc)
                "="
                (codec/url-encode (str v) enc)))
         (interpose "&")
         (apply str))))

(defn build-url [url url-params]
  (str url "?" (make-query-string url-params)))

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))


;; views
(defn home-page
  [request]
  (ring-resp/response "If you've been redirected here you should probably have provided a 'redirect_uri' parameter to /o/authorize/"))

(defn authorize [request]
  (if-let [p (merge (:json-params request) (:params request))]
    (ring-resp/redirect (build-url
                         (:redirect_uri p "/")
                         {:state (:state p "state")
                          :code (rand-str 10)}))
    (ring-resp/response "" 403)
    ))

(defn introspect [request]
  (if (get-in request [:params :token])
    (ring-resp/response {:active true
                         :scope (get-in request [:params :scope] "read write")
                         :exp (+ (rand-int 99999))})
    (ring-resp/response {:active false})
    ))

(defn token [request]
  (ring-resp/response {:access_token (rand-str 20)
                       :scope (get-in request [:params :scope] "read write")
                       :token_type "Bearer"
                       :expires_in (+ (rand-int 99999))
                       })
  )

(def html-interceptors [http/html-body])
(def json-interceptors [http/json-body])

;; Tabular routes
(def routes #{["/" :get (conj html-interceptors `home-page)]
              ["/o/authorize/" :post (conj json-interceptors `authorize) :route-name :authorize-post]
              ["/o/authorize/" :get (conj json-interceptors `authorize) :route-name :authorize]
              ["/o/introspect/" :post (conj json-interceptors `introspect) :route-name :introspect-post]
              ["/o/introspect/" :get (conj json-interceptors `introspect) :route-name :introspect]
              ["/o/token/" :post (conj json-interceptors `token) :route-name :token-post]
              ["/o/token/" :get (conj json-interceptors `token) :route-name :token]
              })

;; Consumed by fakesso.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (read-string (:port env "8080"))
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})

