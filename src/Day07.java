import util.InputUtil;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day07 {

    private Day07() {
    }

    public static void main(final String[] args) {
        final List<Play> plays = InputUtil.splitStream(InputUtil.file("day07"), "\n")
                .map(line -> {
                    final var split = InputUtil.splitHalf(line, " ");
                    final int bid = Integer.parseInt(split.right());
                    final List<Card> cards = InputUtil.splitStream(split.left(), "")
                            .map(Card::from)
                            .toList();

                    return new Play(Hand.from(cards), bid);
                })
                .sorted()
                .toList();

        part1(plays);
    }

    public static void part1(final List<Play> plays) {
        final AtomicInteger ctr = new AtomicInteger(plays.size());

        final long value = plays.stream()
                .sorted()
                .mapToLong(play -> play.bid * ctr.getAndDecrement())
                .sum();

        System.out.println("Part 1: " + value);
    }

    public static void part2() {

    }

    record Play(Hand hand, long bid) {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(10);
            sb.append(this.hand);
            sb.append(" @ ").append(this.bid);
            return sb.toString();
        }
    }

    record Hand(List<Card> cards, Processor processor) {
        static Hand from(final List<Card> cards) {
            return new Hand(cards, Processor.from(cards));
        }

        static Comparator<Hand> getCardComp(final Comparator<Card> cardComp) {
            return Comparator.<Hand, Card>comparing(hand -> hand.cards.get(0), cardComp)
                    .thenComparing(hand -> hand.cards.get(1), cardComp)
                    .thenComparing(hand -> hand.cards.get(2), cardComp)
                    .thenComparing(hand -> hand.cards.get(3), cardComp)
                    .thenComparing(hand -> hand.cards.get(4), cardComp);
        }
    }

    interface Evaluator {
        boolean hasPair(Hand hand);

        boolean hasPairs(Hand hand);

        boolean hasThree(Hand hand);

        boolean hasFull(Hand hand);

        boolean hasFour(Hand hand);

        boolean hasFive(Hand hand);

        Comparator<Play> getComparator();

        record Part1()
                implements Evaluator {
            @Override
            public boolean hasPair(final Hand hand) {
                return hand.processor.contains(2);
            }

            @Override
            public boolean hasPairs(final Hand hand) {
                return hand.processor.contains(2, 2);
            }

            @Override
            public boolean hasThree(final Hand hand) {
                return hand.processor.contains(3);
            }

            @Override
            public boolean hasFull(final Hand hand) {
                return hasThree(hand) && hasPair(hand);
            }

            @Override
            public boolean hasFour(final Hand hand) {
                return hand.processor.contains(4);
            }

            @Override
            public boolean hasFive(final Hand hand) {
                return hand.processor.contains(5);
            }

            @Override
            public Comparator<Play> getComparator() {
                return Comparator.<Play, Eval>comparing(play -> Eval.evaluate(this, play.hand))
                        .thenComparing(play -> play.hand, Hand.getCardComp(Card.STD));
            }
        }

        record Part2()
                implements Evaluator {
            @Override
            public boolean hasPair(final Hand hand) {
                return hand.processor.contains(2) || hand.processor.contains(Card.JACK);
            }

            @Override
            public boolean hasPairs(final Hand hand) {
                return hand.processor.contains(2, 2) || hand.processor.contains(Card.JACK, 2);
            }

            @Override
            public boolean hasThree(final Hand hand) {
                return hand.processor.contains(3) || hasJackSupplement(hand, 3);
            }

            @Override
            public boolean hasFull(final Hand hand) {
                return false;
            }

            @Override
            public boolean hasFour(final Hand hand) {
                return hand.processor.contains(4) || hasJackSupplement(hand, 4);
            }

            @Override
            public boolean hasFive(final Hand hand) {
                return hand.processor.contains(5) || hasJackSupplement(hand, 5);
            }

            private boolean hasJackSupplement(final Hand hand, final int thresh) {
                return (hand.processor.contains(Card.JACK) && hand.processor.contains(
                        5 - hand.processor.count(Card.JACK)));
            }

            @Override
            public Comparator<Play> getComparator() {
                return Comparator.<Play, Eval>comparing(play -> Eval.evaluate(this, play.hand))
                        .thenComparing(play -> play.hand, Hand.getCardComp(Card.AS_JOKER));
            }
        }
    }

    record Processor(Map<Long, Set<Card>> counter, Map<Card, Long> tracker) {
        boolean contains(final long key) {
            return this.counter.containsKey(key);
        }

        boolean contains(final long key, final int count) {
            return contains(key) && this.counter.get(key).size() >= count;
        }

        boolean contains(final Card card) {
            return this.tracker.containsKey(card);
        }

        boolean contains(final Card card, final int count) {
            return contains(card) && count(card) >= count;
        }

        long count(final Card card) {
            return this.tracker.getOrDefault(card, 0L);
        }

        static Processor from(final List<Card> cards) {
            final var tracker = cards
                    .stream()
                    .collect(groupingBy(Function.identity(), counting()));
            final var counter = tracker.entrySet()
                    .stream()
                    .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toSet())));

            return new Processor(counter, tracker);
        }
    }

    enum Card {
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("T"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");

        static Comparator<Card> STD = Comparator.comparingInt(Enum::ordinal);

        static Comparator<Card> AS_JOKER = Comparator.comparingInt(card -> card.equals(JACK) ? -1 : card.ordinal());

        final String pip;

        Card(final String pip) {
            this.pip = pip;
        }

        static Card from(final String pip) {
            for (final Card card : values()) {
                if (card.pip.equals(pip)) {
                    return card;
                }
            }

            throw new RuntimeException();
        }

        @Override
        public String toString() {
            return this.pip;
        }
    }

    enum Eval {
        HIGH((eval, hand) -> true),
        PAIR(Evaluator::hasPair),
        PAIRS(Evaluator::hasPairs),
        THREE(Evaluator::hasThree),
        FULL(Evaluator::hasFull),
        FOUR(Evaluator::hasFour),
        FIVE(Evaluator::hasFive),
        ;

        private final BiPredicate<Evaluator, Hand> doesApply;

        Eval(final BiPredicate<Evaluator, Hand> doesApply) {
            this.doesApply = doesApply;
        }

        boolean applies(final Evaluator evaluator, final Hand hand) {
            return this.doesApply.test(evaluator, hand);
        }

        static Eval evaluate(final Evaluator part, final Hand hand) {
            return Stream.of(values())
                    .sorted(Comparator.<Eval>comparingInt(Enum::ordinal).reversed())
                    .filter(eval -> eval.applies(part, hand))
                    .findFirst()
                    .orElseThrow();
        }
    }
}
