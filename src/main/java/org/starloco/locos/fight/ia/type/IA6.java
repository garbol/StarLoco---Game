package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA6 extends AbstractIA  {

    public IA6(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            if (!Function.INSTANCE.getinvocIfPossible(this.fight, this.fighter)) {
                Fighter friend = Function.INSTANCE.getgetNearestFriend(this.fight, this.fighter);
                Fighter ennemy = Function.INSTANCE.getgetNearestEnnemy(this.fight, this.fighter);

                if (!Function.INSTANCE.getHealIfPossible(this.fight, this.fighter, false)) {
                    if (!Function.INSTANCE.getbuffIfPossible(this.fight, this.fighter, friend)) {
                        if (!Function.INSTANCE.getbuffIfPossible(this.fight, this.fighter, this.fighter)) {
                            if (!Function.INSTANCE.getHealIfPossible(this.fight, this.fighter, true)) {
                                int attack = Function.INSTANCE.getattackIfPossibleAll(fight, this.fighter, ennemy);

                                if (attack != 0) {
                                    if (attack == 5) this.stop = true;
                                    if (Function.INSTANCE.getmoveFarIfPossible(this.fight, this.fighter) != 0) this.stop = true;
                                }
                            }
                        }
                    }
                }
            }

            addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }
}