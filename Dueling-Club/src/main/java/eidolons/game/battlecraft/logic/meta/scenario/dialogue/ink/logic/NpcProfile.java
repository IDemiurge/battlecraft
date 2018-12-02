package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic.TeaMaster.MOOD;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic.TeaMaster.NPC_PROFESSION;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic.TeaMaster.PERSONALITY_TAG;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic.TeaMaster.TEA_PROFILE;

import java.util.Set;

/**
 * Created by JustMe on 11/24/2018.
 */
public class NpcProfile {

    NPC_PROFESSION profession;
    MOOD mood;
    TEA_PROFILE trepidationProfile;
    TEA_PROFILE esteemProfile;
    TEA_PROFILE affectionProfile;
    Set<PERSONALITY_TAG> personalityTags;

    public NpcProfile(NPC_PROFESSION profession, MOOD mood, TEA_PROFILE trepidationProfile, TEA_PROFILE esteemProfile, TEA_PROFILE affectionProfile, Set<PERSONALITY_TAG> personalityTags) {
        this.profession = profession;
        this.mood = mood;
        this.trepidationProfile = trepidationProfile;
        this.esteemProfile = esteemProfile;
        this.affectionProfile = affectionProfile;
        this.personalityTags = personalityTags;
    }
}
