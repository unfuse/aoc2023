import util.InputUtil;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
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
                .toList();

        part1(plays);
        part2(plays);
    }

    public static void part1(final List<Play> plays) {
        System.out.println("Part 1: " + doPart(plays, new Evaluator.Part1()));
    }

    public static void part2(final List<Play> plays) {
        System.out.println("Part 2: " + doPart(plays, new Evaluator.Part2()));
    }

    private static long doPart(final List<Play> plays, final Evaluator evaluator) {
        final AtomicLong ctr = new AtomicLong(plays.size());

        final long value = plays.stream()
                .sorted(evaluator.getComparator().reversed())
                .mapToLong(play -> play.bid * ctr.getAndDecrement())
                .sum();

        return value;
    }

    // A play of the game
    record Play(Hand hand, long bid) {
    }

    // A hand in the game - CardStats is a utility struct that holds cached info for evaluation
    record Hand(List<Card> cards, CardStats cardStats) {
        static Hand from(final List<Card> cards) {
            return new Hand(cards, CardStats.from(cards));
        }

        static Comparator<Hand> getCardComp(final Comparator<Card> cardComp) {
            return Comparator.<Hand, Card>comparing(hand -> hand.cards.get(0), cardComp)
                    .thenComparing(hand -> hand.cards.get(1), cardComp)
                    .thenComparing(hand -> hand.cards.get(2), cardComp)
                    .thenComparing(hand -> hand.cards.get(3), cardComp)
                    .thenComparing(hand -> hand.cards.get(4), cardComp);
        }
    }

    // A way to evaluate if a Hand matches a certain Score criteria
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
                return hand.cardStats.contains(2);
            }

            @Override
            public boolean hasPairs(final Hand hand) {
                return hand.cardStats.contains(2, 2);
            }

            @Override
            public boolean hasThree(final Hand hand) {
                return hand.cardStats.contains(3);
            }

            @Override
            public boolean hasFull(final Hand hand) {
                return hasThree(hand) && hasPair(hand);
            }

            @Override
            public boolean hasFour(final Hand hand) {
                return hand.cardStats.contains(4);
            }

            @Override
            public boolean hasFive(final Hand hand) {
                return hand.cardStats.contains(5);
            }

            @Override
            public Comparator<Play> getComparator() {
                return Comparator.<Play, Score>comparing(play -> Score.evaluate(this, play.hand))
                        .thenComparing(play -> play.hand, Hand.getCardComp(Card.STD));
            }
        }

        record Part2()
                implements Evaluator {
            @Override
            public boolean hasPair(final Hand hand) {
                return hand.cardStats.contains(2) || hand.cardStats.contains(Card.JACK);
            }

            @Override
            public boolean hasPairs(final Hand hand) {
                return hand.cardStats.contains(2, 2) || hand.cardStats.contains(Card.JACK, 2);
            }

            @Override
            public boolean hasThree(final Hand hand) {
                return hand.cardStats.contains(3) || hasJackSupplement(hand, 3);
            }

            @Override
            public boolean hasFull(final Hand hand) {
                final long numJacks = hand.cardStats.count(Card.JACK);
                if (numJacks == 0) {
                    // Normal resolve
                    return hand.cardStats.contains(2) && hand.cardStats.contains(3);
                }

                if (numJacks >= 4) {
                    return true;
                }

                // if jacks >= 2 && TWOS means we can make the triple
                if (numJacks >= 2 && hand.cardStats.containsWithout(2, Card.JACK)) {
                    return true;
                }

                // jacks >= 1
                return hand.cardStats.contains(3) || hand.cardStats.contains(2, 2);
            }

            @Override
            public boolean hasFour(final Hand hand) {
                return hand.cardStats.contains(4) || hasJackSupplement(hand, 4);
            }

            @Override
            public boolean hasFive(final Hand hand) {
                return hand.cardStats.contains(5) || hasJackSupplement(hand, 5);
            }

            private boolean hasJackSupplement(final Hand hand, final int thresh) {
                return (hand.cardStats.contains(Card.JACK) && hand.cardStats.containsWithout(
                        thresh - hand.cardStats.count(Card.JACK), Card.JACK));
            }

            @Override
            public Comparator<Play> getComparator() {
                return Comparator.<Play, Score>comparing(play -> Score.evaluate(this, play.hand))
                        .thenComparing(play -> play.hand, Hand.getCardComp(Card.AS_JOKER));
            }
        }
    }

    // Utility structure that holds
    record CardStats(Map<Long, Set<Card>> cardsByCounts, Map<Card, Long> countPerCard) {
        boolean contains(final long key) {
            return this.cardsByCounts.containsKey(key);
        }

        boolean contains(final long key, final int count) {
            return contains(key) && this.cardsByCounts.get(key).size() >= count;
        }

        boolean containsWithout(final long key, final Card card) {
            return this.cardsByCounts.getOrDefault(key, Collections.EMPTY_SET).stream().anyMatch(c -> !c.equals(card));
        }

        boolean contains(final Card card) {
            return this.countPerCard.containsKey(card);
        }

        boolean contains(final Card card, final int count) {
            return contains(card) && count(card) >= count;
        }

        long count(final Card card) {
            return this.countPerCard.getOrDefault(card, 0L);
        }

        static CardStats from(final List<Card> cards) {
            final var tracker = cards
                    .stream()
                    .collect(groupingBy(Function.identity(), counting()));
            final var counter = tracker.entrySet()
                    .stream()
                    .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toSet())));

            return new CardStats(counter, tracker);
        }
    }

    // Card ranks and their sorting nuances
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

        static final Comparator<Card> STD = Comparator.comparingInt(Enum::ordinal);

        static final Comparator<Card> AS_JOKER = Comparator.comparingInt(
                card -> card.equals(JACK) ? -1 : card.ordinal());

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

    // Score for a hand and how to evaluate it
    enum Score {
        HIGH((eval, hand) -> true),
        PAIR(Evaluator::hasPair),
        PAIRS(Evaluator::hasPairs),
        THREE(Evaluator::hasThree),
        FULL(Evaluator::hasFull),
        FOUR(Evaluator::hasFour),
        FIVE(Evaluator::hasFive),
        ;

        private final BiPredicate<Evaluator, Hand> doesApply;

        Score(final BiPredicate<Evaluator, Hand> doesApply) {
            this.doesApply = doesApply;
        }

        boolean applies(final Evaluator evaluator, final Hand hand) {
            return this.doesApply.test(evaluator, hand);
        }

        static Score evaluate(final Evaluator part, final Hand hand) {
            return Stream.of(values())
                    .sorted(Comparator.<Score>comparingInt(Enum::ordinal).reversed())
                    .filter(score -> score.applies(part, hand))
                    .findFirst()
                    .orElseThrow();
        }
    }
}
