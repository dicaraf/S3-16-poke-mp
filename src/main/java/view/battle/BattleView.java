package view.battle;

/**
 * BattleView receive information about the battle from the controller: after receiving this information, the view is updated
 */
public interface BattleView {
    /**
     * When a pokemon undergo an attack, contoller calls this method to update the life in the view, too
     */
    void setPokemonLife();
    /**
     * When a pokemon undergo an attack, contoller calls this method to update the life bar
     */
    void setPokemonLifeProgressBar(int life, int owner);

    /**
     * When a pokemon is dead, the controller calls this method to set invisible the pokemon for a few seconds
     * @param owner
     */
    void pokemonIsDead(int owner);

    /**
     * Update the view dialogue after other pokemon attacks
     */
    void pokemonWildAttacksAfterTrainerChoice();

    /**
     * Return the attacks of the wild pokemon (or pokemon of other trainer)
     * @return list of attacks
     */
    int[] getOtherPokemonAttacks();
}
