package com.agiletour.repo;

import com.agiletour.entity.Train;
import org.springframework.data.repository.Repository;

public interface TrainRepo extends Repository<Train, Long> {

    Train findById(long id);

    void save(Train train);
}
