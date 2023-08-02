package smartbus.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import smartbus.entity.Character;


public interface CharacterRepository extends ArangoRepository<Character, String> {
}
