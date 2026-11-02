(ns trastes.calculo
  "Cálculo das posições de trastes no temperamento igual de 12 semitons.")

(def formula "d_n = L * (1 - 1 / 2^(n/12))")
(def numero-trastes-padrao 22)

(defn arredondar
  "Arredonda para duas casas decimais."
  [x]
  (/ (Math/round (* (double x) 100.0)) 100.0))

(defn distancia-pestana
  "Distância, em mm, entre a pestana e o traste n."
  [escala n]
  (* escala
     (- 1.0
        (/ 1.0 (Math/pow 2.0 (/ (double n) 12.0))))))

(defn calcular-trastes
  "Retorna as posições, espaçamentos e distâncias à ponte de 1 a n-trastes."
  [escala n-trastes]
  (let [distancias (map #(distancia-pestana escala %)
                        (range (inc n-trastes)))]
    (vec
     (map (fn [n anterior atual]
            {:traste n
             :espacamento_mm (arredondar (- atual anterior))
             :distancia_ponte_mm (arredondar (- escala atual))
             :distancia_pestana_mm (arredondar atual)})
          (range 1 (inc n-trastes))
          distancias
          (rest distancias)))))
