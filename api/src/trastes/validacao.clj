(ns trastes.validacao
  "Conversão e validação dos parâmetros recebidos pela API.")

(def escala-minima 100.0)
(def escala-maxima 1500.0)
(def trastes-minimo 1)
(def trastes-maximo 36)

(defn- ->numero [v]
  (cond
    (number? v) (double v)
    (string? v)
    (try
      (let [s (.replace (.trim ^String v) "," ".")]
        (when-not (.isEmpty ^String s)
          (Double/parseDouble s)))
      (catch NumberFormatException _ nil))
    :else nil))

(defn- finito? [x]
  (and (some? x)
       (not (Double/isNaN x))
       (not (Double/isInfinite x))))

(defn validar-escala [v]
  (let [n (->numero v)]
    (cond
      (nil? v) {:erro "Informe a escala em milímetros (ex.: 650)."}
      (not (finito? n)) {:erro "A escala deve ser um número válido (ex.: 650)."}
      (<= n 0) {:erro "A escala deve ser maior que zero."}
      (or (< n escala-minima) (> n escala-maxima))
      {:erro (format "A escala deve estar entre %.0f e %.0f mm."
                     escala-minima escala-maxima)}
      :else {:valor n})))

(defn validar-trastes [v padrao]
  (if (or (nil? v)
          (and (string? v) (empty? (.trim ^String v))))
    {:valor padrao}
    (let [n (->numero v)]
      (cond
        (or (not (finito? n))
            (not= n (Math/floor n)))
        {:erro "A quantidade de trastes deve ser um número inteiro."}
        (or (< n trastes-minimo) (> n trastes-maximo))
        {:erro (format "A quantidade de trastes deve estar entre %d e %d."
                       trastes-minimo trastes-maximo)}
        :else {:valor (long n)}))))
