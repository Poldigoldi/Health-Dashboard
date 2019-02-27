package Healthdata;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerInformationRepository extends MongoRepository<ServerInformation, String> {}
