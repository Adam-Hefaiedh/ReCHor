package ch.epfl.rechor;

/**
 * Une classe verifiant si une condition a ete accompli.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     * Verifie si la condition est validé sinon envoie une erreur.
     *
     * @param shouldBeTrue La condition a verifier.
     * @throws IllegalArgumentException si shouldBeTrue n'est pas vrai.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
