package ch.epfl.rechor;

/**
 * Une classe verifiant si une condition a ete accompli.
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     *Verifie si la condition est valider sinon envoie une erreur.
     *
     * @param shouldBeTrue
     * La condition a verifier.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
