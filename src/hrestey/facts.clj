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


;;handle daedalus upgrades for each weapon and aspect

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


;; upgrades for varatha available for all aspects
(def varatha-upgrades-base
  #{'extending-jab
    'breaching-skewer
    'massive-spin
    'quick-spin
    'serrated-point
    'flaring-spin
    'triple-jab})

(def varatha-upgrades-zagreus
  (conj varatha-upgrades-base 'chain-skewer 'viscious-skewer 'exploding-launcher 'flurry-jab 'charged-skewer))

(def varatha-upgrades-achilles
  (conj varatha-upgrades-base 'viscious-skewer 'flurry-jab))

(def varatha-upgrades-hades
  (conj varatha-upgrades-base 'chain-skewer 'viscious-skewer 'exploding-launcher 'charged-skewer))

(def varatha-upgrades-guan-yu
  (conj varatha-upgrades-base 'charged-skewer 'winged-serpent))


(def aegis-upgrades-base
  #{'sudden-rush
    'pulverizing-blow
    'dashing-wallop
    'explosive-return
    'minotaur-rush
    'breaching-rush
    'charged-shot
    'empowering-flight
    'ferocious-guard})

(def aegis-upgrades-zagreus
  (conj aegis-upgrades-base 'dread-flight 'charged-flight 'dashing-flight))

(def aegis-upgrades-chaos
  (conj aegis-upgrades-base 'dread-flight 'dashing-flight))

(def aegis-upgrades-beowulf
  (conj aegis-upgrades-zagreus 'unyielding-defense))


(def coronacht-upgrades-base
  #{'twin-shot
    'sniper-shot
    'explosive-shot
    'perfect-shot
    'relentless-volley
    'triple-shot
    'chain-shot
    'point-blank-shot})

(def coronacht-upgrades-main
  (conj coronacht-upgrades-base 'flurry-shot 'piercing-volley 'charged-volley 'concentrated-volley))

(def coronacht-upgrades-chiron
  (conj coronacht-upgrades-base 'flurry-shot 'piercing-volley 'concentrated-volley))

(def coronacht-upgrades-rama
  (conj coronacht-upgrades-base 'repulse-shot))


(def malphon-upgrades-base
  #{'breaching-cross
    'draining-cutter
    'concentrated-knuckle
    'explosive-upper
    'colossal-knuckle})

(def malphon-upgrades-zagreus
  (conj malphon-upgrades-base 'rolling-knuckle 'long-knuckle 'flying-cutter 'rush-kick 'quake-cutter 'kinetic-launcher 'heavy-knuckle))

(def malphon-upgrades-talos
  (conj malphon-upgrades-base 'rolling-knuckle 'heavy-knuckle 'long-knuckle 'quake-cutter))

(def malphon-upgrades-demeter
  (conj malphon-upgrades-base 'rolling-knuckle 'long-knuckle 'heavy-knuckle 'kinetic-launcher 'rush-kick 'flying-cutter))

(def malphon-upgrades-gilgamesh
  (conj malphon-upgrades-base 'flying-cutter 'rush-kick 'quake-cutter 'rending-claws))


(def weapon-facts-with-daedalus
  (reduce #(setup-daedalus-facts-for-aspect %1 (first %2) (second %2) (last %2))
          weapon-facts-base
          [['stygius 'zagreus stygius-upgrades-main]
           ['stygius 'nemesis stygius-upgrades-main]
           ['stygius 'poseidon stygius-upgrades-main]
           ['stygius 'arthur stygius-upgrades-arthur]
           ['varatha 'zagreus varatha-upgrades-zagreus]
           ['varatha 'achilles varatha-upgrades-achilles]
           ['varatha 'hades varatha-upgrades-hades]
           ['varatha 'guan-yu varatha-upgrades-guan-yu]
           ['aegis 'zagreus aegis-upgrades-zagreus]
           ['aegis 'chaos aegis-upgrades-chaos]
           ['aegis 'zeus aegis-upgrades-base]
           ['aegis 'beowulf aegis-upgrades-beowulf]
           ['coronacht 'zagreus coronacht-upgrades-main]
           ['coronacht 'chiron coronacht-upgrades-chiron]
           ['coronacht 'hera coronacht-upgrades-main]
           ['coronacht 'rama coronacht-upgrades-rama]
           ['malphon 'zagreus malphon-upgrades-zagreus]
           ['malphon 'talos malphon-upgrades-talos]
           ['malphon 'demeter malphon-upgrades-demeter]
           ['malphon 'gilgamesh malphon-upgrades-gilgamesh]]))


;; weapon stats
