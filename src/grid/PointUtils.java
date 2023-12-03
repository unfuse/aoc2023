package grid;

class PointUtils {

    private PointUtils() {
    }

    static <P extends Point<Integer>> boolean isCardinal(final P p1, final P p2) {
        return p1.minus(p2).stream().map(Math::abs).reduce(0, Math::addExact) == 1;
    }

    static <P extends Point<Integer>> boolean isOrthogonal(final P p1, final P p2) {
        return p1.minus(p2).stream().allMatch(dim -> dim <= 1);
    }
}
