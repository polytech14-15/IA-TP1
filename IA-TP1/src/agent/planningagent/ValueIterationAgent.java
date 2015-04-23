package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.gridworld.ActionGridworld;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.HashMapUtil;

/**
 * Cet agent met a jour sa fonction de valeur avec value iteration et choisit
 * ses actions selon la politique calculee.
 *
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent {
    //*** VOTRE CODE
    HashMapUtil v;
    Double gamma;

    /**
     *
     * @param gamma
     * @param mdp
     */
    public ValueIterationAgent(double gamma, MDP mdp) {
        super(mdp);
        //*** VOTRE CODE
        this.gamma = gamma;
        this.v = new HashMapUtil();
        reset();
    }

    public ValueIterationAgent(MDP mdp) {
        this(0.9, mdp);

    }

    /**
     *
     * Mise a jour de V: effectue UNE iteration de value iteration
     */
    @Override
    public void updateV() {
        this.delta = 0.0;
        //*** VOTRE CODE
        HashMapUtil v_clone = (HashMapUtil) this.v.clone();
        // Pour chaque etats
        for (Etat e : this.mdp.getEtatsAccessibles()) {
            double max = 0;
            // Pour chaque action de cet etat
            for (Action a : this.mdp.getActionsPossibles(e)) {
                Double sum = 0.0;
                try {
                    Map<Etat, Double> etp_map = this.mdp.getEtatTransitionProba(e, a);
                    // Realise la somme
                    for (Etat ebis : etp_map.keySet()) {
                        sum += etp_map.get(ebis) * (this.mdp.getRecompense(e, a, ebis) + this.gamma * v_clone.get(ebis));
                    }
                } catch (IllegalActionException ex) {
                    Logger.getLogger(ValueIterationAgent.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ValueIterationAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Check la valeur max
                max = Math.max(max, sum);
            }
            // Mise a jour valeur
            this.v.put(e, max);
        }

        // Mise a jour delta
        Double diff_max = 0.0;
        for (Etat e : this.v.keySet()) {
            diff_max = Math.max(this.v.get(e), v_clone.get(e));
        }
        this.delta = diff_max;

        // mise a jour vmax et vmin pour affichage
        // ...
        Double t_vmin = Double.MAX_VALUE;
        Double t_vmax = Double.MIN_VALUE;
        for (Double v_value : this.v.values()) {
            t_vmin = Math.min(t_vmin, v_value);
            t_vmax = Math.max(t_vmax, v_value);
        }
        this.vmin = t_vmin;
        this.vmax = t_vmax;

        //******************* a laisser a la fin de la methode
        this.notifyObs();
    }

    /**
     * renvoi l'action donnee par la politique
     */
    @Override
    public Action getAction(Etat e) {
        //*** VOTRE CODE
        List<Action> actions = getPolitique(e);
        if (actions.size() == 1) {
            return actions.get(0);
        } else if (actions.size() > 0) {
            Random rdm = new Random(System.currentTimeMillis());
            return actions.get(rdm.nextInt(actions.size()));
        } else {
            return ActionGridworld.NONE;
        }
    }

    @Override
    public double getValeur(Etat _e) {
        //*** VOTRE CODE
        return this.v.get(_e);
    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans etat (plusieurs actions
     * sont renvoyees si valeurs identiques, liste vide si aucune action n'est
     * possible)
     */
    @Override
    public List<Action> getPolitique(Etat _e) {
        //*** VOTRE CODE
        List<Action> l = new ArrayList<>();
        List<Action> t_l = this.mdp.getActionsPossibles(_e);
        double maxvalue = 0;
        for (Action a : t_l) {
            try {
                HashMap<Etat, Double> hash = (HashMap<Etat, Double>) this.mdp.getEtatTransitionProba(_e, a);
                double sum = 0;
                for (Etat dEtat : hash.keySet()) {
                    sum += hash.get(dEtat) * (this.mdp.getRecompense(_e, a, dEtat) + this.gamma * this.v.get(dEtat));
                }
                if (sum > maxvalue) {
                    maxvalue = sum;
                    l.clear();
                    l.add(a);
                } else if (sum == maxvalue) {
                    l.add(a);
                }
            } catch (IllegalActionException ex) {
                Logger.getLogger(ValueIterationAgent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ValueIterationAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return l;
    }

    @Override
    public void reset() {
        super.reset();
        //*** VOTRE CODE
        // Initialise la map v
        for (Etat e : this.mdp.getEtatsAccessibles()) {
            this.v.put(e, 0.0);
        }

        this.vmin = Double.MAX_VALUE;
        this.vmax = Double.MAX_VALUE;

        /*-----------------*/
        this.notifyObs();

    }

    @Override
    public void setGamma(double arg0) {
        //*** VOTRE CODE
        this.gamma = arg0;
    }

}
