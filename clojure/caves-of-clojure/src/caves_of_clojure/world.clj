(ns caves-of-clojure.world)

(def world-size [160 50])

(defrecord World [tiles])
(defrecord Tile [kind glyph color])

(def tiles
  {:floor (map->Tile {:kind :floor :glyph "." :color :white})
   :wall  (map->Tile {:kind :wall  :glyph "#" :color :red})
   :bound (map->Tile {:kind :bound :glyph "X" :color :black})})

(defn get-tile [tiles x y]
  (get-in tiles [y x] (:bound tiles)))

(defn random-tiles []
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor :wall])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))

(defn random-world []
  (map->World {:tiles (random-tiles)}))

(defn print-row [row]
  (println (apply str (map :glyph row))))

(defn print-world [world]
  (dorun (map print-row (:tiles world))))
