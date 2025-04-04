package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class representant un voyage
 * @param legs
 * les étapes du voyage.
 *
 */

public record Journey(List<Leg> legs) {
    /**
     * Le constructeur verifie que :
     * elle n'est pas vide,
     * les étapes à pied alternent avec celles en transport,
     * pour toutes les étapes sauf la première, l'instant de départ ne précède pas celui d'arrivée de la précédente,
     * pour toutes les étapes sauf la première, l'arrêt de départ est identique à l'arrêt d'arrivée de la précédente.
     * @param legs les étapes du voyage.
     *
     * @throws IllegalArgumentException si un parametre n'est pas valide
     */
    public Journey{
        Preconditions.checkArgument(!legs.isEmpty());
        for(int i=1; i<legs.size(); i++)
        {
            Preconditions.checkArgument(!legs.get(i-1).getClass().equals(legs.get(i).getClass()));
            Preconditions.checkArgument(!legs.get(i).depTime().isBefore(legs.get(i-1).arrTime()));
            Preconditions.checkArgument(legs.get(i).depStop().equals(legs.get(i-1).arrStop()));
        }
        legs = List.copyOf(legs);
    }

    /**
     * Retourne l'arrêt de départ du voyage.
     * @return l'arrêt de la premiere etape
     */
    public Stop depStop()
    {
        return legs.getFirst().depStop();
    }

    /**
     * Retourne l'arrêt d'arrivée du voyage.
     * @return l'arrêt d'arrivée de la derniere etape
     */
    public Stop arrStop()
    {
        return legs.getLast().arrStop();
    }

    /**
     *  Retourne la date/heure de début du voyage,
     * @return la date/heure de la premiere etape
     */
    public LocalDateTime depTime(){
        return legs.getFirst().depTime();
    }

    /**
     * Retourne la date/heure de fin du voyage
     * @return la date/heure de la derniere etape
     */
    public LocalDateTime arrTime(){
        return legs.getLast().arrTime();
    }

    /**
     * Retourne la durée totale du voyage
     * @return la durée qui separe la date/heure de fin de celle de début.
     */
    public Duration duration(){
        return Duration.between(depTime(), arrTime());
    }

    /**
     * Represente les etapes du voyage
     */
    public sealed interface Leg{
        /**
         * Retourne l'arrêt de départ de l'étape.
         * @return l'arrêt de départ de l'étape
         */
        abstract Stop depStop();
        /**
         * Retourne l'arrêt d'arrivée de l'étape.
         * @return l'arrêt d'arrivée de l'étape
         */
        abstract Stop arrStop();
        /**
         * Retourne la date/heure de départ de l'étape.
         * @return la date/heure de départ de l'étape.
         */
        abstract LocalDateTime depTime();

        /**
         * Retourne la date/heure d'arrivée de l'étape
         * @return la date/heure d'arrivée de l'étape
         */
        abstract LocalDateTime arrTime();

        /**
         * Retourne la liste des arrêts intermédiaires de l'étape
         * @return la liste des arrêts intermédiaires de l'étape
         */
        abstract List<IntermediateStop> intermediateStops();

        /**
         * Retourne la durée de l'étape.
         * @return la durée de l'étape
         */
        default Duration duration(){
            return Duration.between(depTime(), arrTime());
        }

        /**
         * Class representant un arrêt auquel le moyen de transport utilisé s'arrête effectivement,
         * mais qui se trouve entre l'arrêt de départ et l'arrêt d'arrivée de l'étape.
         * @param stop l'arrêt intermédiaire en question
         * @param arrTime la date/heure d'arrivée à l'arrêt,
         * @param depTime la date/heure de départ de l'arrêt.
         */
        public record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime){
            /**
             * Le constructeur verifie que stop n'est pas null et
             * la date/heure de départ n'est pas antérieure à celle d'arrivée.
             * @param stop l'arrêt intermédiaire en question
             * @param arrTime la date/heure d'arrivée à l'arrêt,
             * @param depTime la date/heure de départ de l'arrêt.
             *
             * @throws NullPointerException si Stop est null
             * @throws IllegalArgumentException si le temps d'arriver est avant le temps de depart
             */
            public IntermediateStop{
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(!arrTime.isAfter(depTime));
            }
        }

        /**
         * Class représentant une étape effectuée en transport public
         * @param depStop l'arrêt de départ de l'étape
         * @param depTime la date/heure de départ de l'étape
         * @param arrStop l'arrêt d'arrivée de l'étape
         * @param arrTime la date/heure d'arrivée de l'étape
         * @param intermediateStops les éventuels arrêts intermédiaires de l'étape
         * @param vehicle le type de véhicule utilisé pour cette étape
         * @param route le nom de la ligne sur laquelle circule le véhicule utilisé pour cette étape
         * @param destination le nom de la destination finale du véhicule utilisé pour cette étape
         */
        public record Transport(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime, List<IntermediateStop> intermediateStops, Vehicle vehicle, String route, String destination) implements Leg{
            /**
             * Le constructeur verifie qu'aucun argument n'est null et
             * que la date/heure d'arrivée n'est pas antérieure à celle de départ.
             * @param depStop l'arrêt de départ de l'étape
             * @param depTime la date/heure de départ de l'étape
             * @param arrStop l'arrêt d'arrivée de l'étape
             * @param arrTime la date/heure d'arrivée de l'étape
             * @param intermediateStops les éventuels arrêts intermédiaires de l'étape
             * @param vehicle le type de véhicule utilisé pour cette étape
             * @param route le nom de la ligne sur laquelle circule le véhicule utilisé pour cette étape
             * @param destination le nom de la destination finale du véhicule utilisé pour cette étape
             * @throws NullPointerException si une des valeurs est null
             * @throws IllegalArgumentException si le temps d'arriver est apres le temps de depart
             */
            public Transport{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(vehicle);
                Objects.requireNonNull(route);
                Objects.requireNonNull(destination);
                Preconditions.checkArgument(!depTime.isAfter(arrTime));
                intermediateStops = List.copyOf(intermediateStops);
            }
        }

        /**
         * Class représentant une étape effectuée à pied
         * @param depStop l'arrêt de départ de l'étape
         * @param depTime la date/heure de départ de l'étape
         * @param arrStop l'arrêt d'arrivée de l'étape
         * @param arrTime la date/heure d'arrivée de l'étape
         */
        public record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime) implements Leg{
            /**
             * Le constructeur verifie qu'aucun argument n'est null et
             * que la date/heure d'arrivée n'est pas antérieure à celle de départ.
             * @param depStop l'arrêt de départ de l'étape
             * @param depTime la date/heure de départ de l'étape
             * @param arrStop l'arrêt d'arrivée de l'étape
             * @param arrTime la date/heure d'arrivée de l'étape
             * @throws IllegalArgumentException si l'heure de depart est apres l'heure d'arriver
             * @throws NullPointerException si les arrets sont null
             */
            public Foot{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Preconditions.checkArgument(!depTime.isAfter(arrTime));
            }

            /**
             * Qui implémente la méthode abstraite correspondante de l'interface Leg
             * @return une liste vide, car il n'y a pas d'arrêts intermédiaires
             */
            public List<IntermediateStop> intermediateStops(){
                return Collections.emptyList();
            }

            /**
             * Retourne vrai ssi l'étape est un changement au sein de la même gare
             * @return vrai si si le nom de l'arrêt de départ est identique à celui de l'arrêt d'arrivée
             */
            public boolean isTransfer(){
                return Objects.equals(depStop.name(), arrStop.name());
            }
        }
    }
}
