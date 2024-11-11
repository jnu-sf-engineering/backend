package com.momentum.repository;

import com.momentum.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CardRepository extends JpaRepository<Card,Long> {
}
