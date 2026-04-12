package com.hzsca.service;

import com.hzsca.entity.Animals;

import java.util.List;

public interface MatchService {
    List<Recommendation> recommend(Long userId, int limit);

    class Recommendation {
        private Animals cat;
        private Double score;
        private List<String> reasons;

        public Recommendation() {
        }

        public Recommendation(Animals cat, Double score, List<String> reasons) {
            this.cat = cat;
            this.score = score;
            this.reasons = reasons;
        }

        public Animals getCat() {
            return cat;
        }

        public void setCat(Animals cat) {
            this.cat = cat;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public List<String> getReasons() {
            return reasons;
        }

        public void setReasons(List<String> reasons) {
            this.reasons = reasons;
        }
    }
}
