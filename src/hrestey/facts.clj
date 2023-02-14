(ns hrestey.facts
  (:require [clojure.core.logic.pldb :as pldb]
            [clojure.core.logic :as l]))


;; model weapons -- "Infernal Arms"
(pldb/db-rel weapon w)
(pldb/db-rel aspect w a)
(pldb/db-rel daedalus w a u) ;; daedalus hammer upgrades
(pldb/db-rel unlocked? w l) ;; is weapon available for selection?
(pldb/db-rel titan-blood w a b) ;; titan's blood spent for upgrades on each aspect

(def weapon-facts-base
  (pldb/db
   [weapon 'stygius]
   [weapon 'varatha]
   [weapon 'aegis]
   [weapon 'coronacht]
   [weapon 'malphon]
   [weapon 'exagryph]

   [unlocked? 'stygius true]
   [unlocked? 'varatha true]
   [unlocked? 'aegis true]
   [unlocked? 'coronacht true]
   [unlocked? 'malphon false]
   [unlocked? 'exagryph false]

   [aspect 'stygius ['zagreus 'nemesis 'poseidon 'arthur]]
   [aspect 'varatha ['zagreus 'achilles 'hades 'guan-yu]]
   [aspect 'aegis ['zagreus 'chaos 'zeus 'beowulf]]
   [aspect 'coronacht ['zagreus 'chiron 'hera 'rama]]
   [aspect 'malphon ['zagreus 'eris 'demeter 'gilgamesh]]
   [aspect 'exagryph ['zagreus]]))


(pldb/with-db weapon-facts-base
  (l/run* [q]
    (l/fresh [a]
      (aspect 'stygius a)
      (l/== q a)))
  (l/run* [q]
    (l/fresh [stygius varatha aegis coronacht malphon exagryph]
      (unlocked? 'stygius stygius)
      (unlocked? 'varatha varatha)
      (unlocked? 'aegis aegis)
      (unlocked? 'coronacht coronacht)
      (unlocked? 'malphon malphon)
      (unlocked? 'exagryph exagryph)
      (l/== q [stygius varatha aegis coronacht malphon exagryph]))))

(defn- setup-daedalus-facts-for-aspect
  [base w a u]
  (pldb/db-fact base daedalus w a u))

;; daedalus upgrades for stygius that are available for all move-sets/aspects
(def stygius-upgrades-base 
  #{'breaching-slash
    'cursed-slash
    'dash-nova
    'double-edge
    'double-nova
    'hoarding-slash
    'piercing-wave
    'shadow-slash
    'super-nova})

;; daedalus upgrades for stygius that are available for main move-set/the aspects other than arthur's
(def stygius-upgrades-main
  (conj stygius-upgrades-base 'cruel-thrust 'flurry-slash 'world-splitter))

;; daedalus upgrades for stygius in the aspect of arthur
(def stygius-upgrades-arthur
  (conj stygius-upgrades-base 'greater-consecration))

(-> weapon-facts-base
    (setup-daedalus-facts-for-aspect 'stygius 'zagreus stygius-upgrades-main)
    (setup-daedalus-facts-for-aspect 'stygius 'nemesis stygius-upgrades-main)
    (setup-daedalus-facts-for-aspect 'stygius 'poseidon stygius-upgrades-main)
    (setup-daedalus-facts-for-aspect 'stygius 'arthur stygius-upgrades-arthur))
