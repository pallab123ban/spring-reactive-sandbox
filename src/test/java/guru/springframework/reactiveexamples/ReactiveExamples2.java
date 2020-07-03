package guru.springframework.reactiveexamples;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactiveExamples2 {

  Person pallab = new Person("Pallab", "Banerjee");
  Person pritha = new Person("Pritha", "Chakraborty");
  Person rishik = new Person("Rishik", "Banerjee");
  Person zach = new Person("Zach", "Doe");
  Person jane = new Person("Jane", "Doe");

  @Test
  public void testMono() {
    Mono<Person> personMono = Mono.just(pallab);

    Person person = personMono.block();

    log.info(person.sayMyName());
  }

  @Test
  public void testMonoTypeTransformation() {
    Mono<Person> personMono = Mono.just(pritha);

    PersonCommand personCommand = personMono
                                      .map(person ->
                                      {
                                        //type transformation takes place here (person -> personcommand)
                                        return new PersonCommand(person);
                                      }).block();
    log.info(personCommand.sayMyName());
  }

  @Test(expected = NullPointerException.class)
  public void testMonoFilter() {
    Mono<Person> personMono = Mono.just(zach);

    Person person = personMono
                            .filter(person1 -> person1.getFirstName().equalsIgnoreCase("xyz"))
                            .block();
    log.info(person.sayMyName());
  }

  @Test
  public void fluxTest() {
    Flux<Person> personFlux = Flux.just(pallab, pritha, rishik, zach, jane);

    personFlux.subscribe(person -> log.info(person.sayMyName()));
  }

  @Test
  public void fluxFilterTest() {
    Flux<Person> personFlux = Flux.just(pallab, pritha, rishik, zach, jane);

    personFlux.filter(person -> person.getFirstName().equals(pritha.getFirstName()))
              .subscribe(person -> log.info(person.sayMyName()));
  }

  @Test
  public void testFluxDelayNoOutput() {
    Flux<Person> personFlux = Flux.just(pallab, pritha, rishik, zach, jane);

    personFlux.delayElements(Duration.ofSeconds(1))
              .subscribe(person -> log.info(person.sayMyName()));
  }

  @Test
  public void testFluxDelayWithCountDownLatch() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);

    Flux<Person> personFlux = Flux.just(pallab, pritha, rishik, zach, jane);

    personFlux.delayElements(Duration.ofSeconds(1))
              .doOnComplete(countDownLatch::countDown)
              .subscribe(person -> log.info(person.sayMyName()));

    countDownLatch.await();
  }

  @Test
  public void testFluxCountDownLatchFilter() throws InterruptedException {

    CountDownLatch countDownLatch = new CountDownLatch(1);

    Flux<Person> personFlux = Flux.just(pallab, pritha, rishik, zach, jane);

    personFlux.delayElements(Duration.ofSeconds(1))
        .filter(person -> person.getFirstName().contains("i"))
        .doOnComplete(countDownLatch::countDown)
        .subscribe(person -> log.info(person.sayMyName()));

    countDownLatch.await();
  }

}
