(ns trastes.core
  "API REST para cálculo das posições dos trastes."
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [json-response wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response status]]
            [trastes.calculo :as calculo]
            [trastes.validacao :as validacao])
  (:gen-class))

(defn- erro [codigo mensagem]
  (-> (response {:erro mensagem})
      (status codigo)))

(defn processar [escala-bruta trastes-bruto]
  (let [esc (validacao/validar-escala escala-bruta)
        tra (validacao/validar-trastes trastes-bruto
                                      calculo/numero-trastes-padrao)]
    (cond
      (:erro esc) (erro 400 (:erro esc))
      (:erro tra) (erro 400 (:erro tra))
      :else
      (let [escala (:valor esc)
            n (:valor tra)]
        (response {:escala_mm escala
                   :formula calculo/formula
                   :quantidade_trastes n
                   :trastes (calculo/calcular-trastes escala n)})))))

(defroutes rotas
  (GET "/api/health" []
    (response {:status "ok"}))

  (GET "/api/trastes" [escala trastes]
    (processar escala trastes))

  (POST "/api/trastes" {body :body}
    (if (map? body)
      (processar (get body "escala") (get body "trastes"))
      (erro 400 "O corpo deve ser um objeto JSON válido.")))

  (route/not-found (erro 404 "Rota não encontrada.")))

(defn- wrap-erros [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (.printStackTrace e)
        ;; Este middleware envolve o serializador; o erro também precisa
        ;; ser serializado explicitamente para manter a resposta em JSON.
        (json-response (erro 500 "Erro interno do servidor.") {})))))

(def app
  (-> rotas
      wrap-params
      (wrap-json-body {:malformed-response (erro 400 "JSON inválido.")})
      wrap-json-response
      wrap-erros
      ;; API pública e sem cookies/autenticação: aceita origens externas.
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :options]
                 :access-control-allow-headers ["Content-Type"])))

(defn -main [& _]
  (let [porta (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println (str "API de trastes iniciada na porta " porta))
    (run-jetty app {:port porta :host "0.0.0.0" :join? true})))
