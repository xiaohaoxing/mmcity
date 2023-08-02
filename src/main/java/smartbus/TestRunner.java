package smartbus;

import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import smartbus.entity.Character;
import smartbus.repository.CharacterRepository;

import java.util.Optional;

@ComponentScan("smartbus")
public class TestRunner implements CommandLineRunner {
    @Autowired
    private ArangoOperations operations;
    @Autowired
    private CharacterRepository characterRepository;

    @Override
    public void run(String... args) throws Exception {
        operations.dropDatabase();
        final Character xhx = new Character("haoxing", "xiao", true, 27);
//        characterRepository.save(xhx);
        // the generated id from the database is set in the original entity
        System.out.println(String.format("Ned Stark saved in the database with id: '%s'", xhx.getId()));

        // lets take a look whether we can find Ned Stark in the database
//        final Optional<Character> foundXhx = characterRepository.findById(xhx.getId());
//        assert foundXhx.isPresent();
//        System.out.println(String.format("Found %s", foundXhx.get()));
    }
}
