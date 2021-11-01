package com.example.javaplayground;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by cuongnghiem on 01/11/2021
 **/
@Slf4j
public class ReactiveProgrammingTests {

    @Test
    public void subscribeToAStream(){
        List<Integer> list = new ArrayList<>();
        Flux<Integer> flux = Flux.just(5, 3, 2, 7, 9, 1);
        flux.log().subscribe(a -> list.add(a));

        assertThat(list).containsExactly(5, 3, 2, 7, 9, 1);
    }

    @Test
    public void subscribeToAStreamVer2(){
        List<Integer> list = new ArrayList<>();
        Flux.just(3, 5, 7, 9, 2, 4, 6)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @SneakyThrows
                    @Override
                    public void onNext(Integer integer) {
                        list.add(integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                        log.info("Stream Completed");
                    }
                });

        assertThat(list).containsExactly(3, 5, 7, 9, 2, 4, 6);
    }

    @Test
    public void backpressureDemo(){
        int numberPerRequest = 3;
        List<Integer> lists = new ArrayList<>();
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    private int current;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        current = 0;
                        s.request(numberPerRequest);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        lists.add(integer);
                        current++;
                        if (current % numberPerRequest == 0)
                            s.request(numberPerRequest);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        assertThat(lists).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void operatingOnStream(){
        List<Integer> list = new ArrayList<>();
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
                .log()
                .filter(e -> e % 2 != 0)
                .subscribe(list::add);

        assertThat(list).containsExactly(1, 3, 5, 7, 9, 11);
    }

    @Test
    public void combiningStream() {
        List<String> list = new ArrayList<>();
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .log()
                .zipWith(Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"), (e1, e2) -> {
                    log.debug("Zipping " + e1 + " - " + e2);
                    return e2.concat(String.format(": %d", e1));
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        log.debug("Adding to list " + s);
                        list.add(s);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        assertThat(list).contains("a: 1");
    }

    // above stream is called cold stream, which is static, in a realistic scenario, stream is infinite.
    // Which is called hot stream.
    @Test
    public void connectableFlux() {

        // simulate infinite stream.
        ConnectableFlux<Long> publisher = Flux.create(new Consumer<FluxSink<Long>>() {
            private long number = 0;
            @Override
            public void accept(FluxSink<Long> longFluxSink) {
                while (true) {
                    longFluxSink.next(number++);
                }
            }
        }).publish();

        Subscriber<Long> sub1 = new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(100);
            }

            @Override
            public void onNext(Long aLong) {
                System.out.println("Subscriber 1 working on " + aLong);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        Subscriber<Long> sub2 = new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(100);
            }

            @Override
            public void onNext(Long aLong) {
                System.out.println("Subscriber 2 working on " + aLong);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        // we can add multiple subscription.
        publisher.subscribe(sub1);
        publisher.subscribe(sub2);

        // when calling connect, flux will start emitting
        publisher.connect();
    }

    // when too much data is being passed to consumer, we will reduce the amount of data being sent
    // to downstream.
    @Test
    public void throttleStrategies(){
        // simulate infinite data stream
        Flux<Object> publisher = Flux.create(fluxSink -> {
            while (true)
                fluxSink.next(System.currentTimeMillis());
        });

        publisher.sample(Duration.ofSeconds(1))
                .subscribe(System.out::println);
    }

    @Test
    public void concurrencyFlux() {
        Flux.range(0, 100)
                .log()
                .subscribeOn(Schedulers.parallel())
                .subscribe(System.out::println);

        log.debug("Main thread is ended");
    }
}
