package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter.MissionConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class VanillaMissionsImporter
{
    private final MissionConverter missionConverter;
    private final MissionRepository missionRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event)
    {
        importVanillaMissions();
    }

    private void importVanillaMissions()
    {
        final List<Mission> missions = new ArrayList<>();
        missions.addAll(vanillaMissions());
        missions.addAll(maldenMissions());
        missions.addAll(apexMissions());
        missions.addAll(contactMissions());

        // Check if missions already exists, if not, add them
        List<MissionEntity> existingMissionEntities = missionRepository.findAll();
        List<Mission> notInstalledVanillaMissions = findNotInstalledTemplates(existingMissionEntities, missions);
        if (notInstalledVanillaMissions.isEmpty())
            return;

        log.info("Importing vanilla missions: {}", notInstalledVanillaMissions);
        notInstalledVanillaMissions.stream()
                .map(this.missionConverter::convertToEntity)
                .forEach(this.missionRepository::save);
    }

    private List<Mission> findNotInstalledTemplates(List<MissionEntity> existingMissionEntities, List<Mission> vanillaMissions)
    {
        List<String> installedMissions = existingMissionEntities.stream().map(MissionEntity::getTemplate).toList();
        return vanillaMissions.stream()
                .filter(vanillaMission -> !installedMissions.contains(vanillaMission.getTemplate()))
                .toList();
    }

    private List<Mission> vanillaMissions()
    {
        final List<Mission> vanillaMissions = new ArrayList<>();
        vanillaMissions.add(Mission.builder()
                .name("ZGM 4+1 Bootcamp")
                .template("MP_Bootcamp_01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 10 Escape From Stratis")
                .template("MP_COOP_m01.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 07 Headhunters")
                .template("MP_COOP_m02.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 10 Escape From Altis")
                .template("MP_COOP_m03.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZvP 10+1 Defend Kamino")
                .template("MP_COOP_m04.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZvP 10+1 Defend Syrta")
                .template("MP_COOP_m05.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZvP 10+1 Seize Edoris")
                .template("MP_COOP_m06.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZvP 10+1 Seize Feres")
                .template("MP_COOP_m07.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 12 Tanks")
                .template("MP_COOP_m08.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("CTF 32 End Game Feres")
                .template("MP_End_Game_02.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("CTF 16 End Game Zaros")
                .template("MP_End_Game_03.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 06 Support Katalak")
                .template("MP_GroundSupport01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 08 Support Sofia")
                .template("MP_GroundSupport02.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 08 Support Pyrgos")
                .template("MP_GroundSupport03.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 04 Support Rodopoli")
                .template("MP_GroundSupport04.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 05 Support Stratis")
                .template("MP_GroundSupport05.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("CTF 16 End Game Kavala")
                .template("MP_Marksmen_01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 48+2 Master Altis")
                .template("MP_ZGM_m11.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Altis (CSAT)")
                .template("MP_ZGM_m11_EAST.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Altis (AAF)")
                .template("MP_ZGM_m11_GUER.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Altis (NATO)")
                .template("MP_ZGM_m11_WEST.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 48+2 Master Stratis")
                .template("MP_ZGM_m12.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Stratis (CSAT)")
                .template("MP_ZGM_m12_EAST.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Stratis (AAF)")
                .template("MP_ZGM_m12_GUER.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Stratis (NATO)")
                .template("MP_ZGM_m12_WEST.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 48+2 Master Virtual Reality")
                .template("MP_ZGM_m13.VR")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Virtual Reality (CSAT)")
                .template("MP_ZGM_m13_EAST.VR")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Virtual Reality (AAF)")
                .template("MP_ZGM_m13_GUER.VR")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZGM 16+2 Master Virtual Reality (NATO)")
                .template("MP_ZGM_m13_WEST.VR")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZR 8+1 Race on Altis (Karts)")
                .template("MP_ZR_8_Karts01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZR 8+1 Race on Stratis (Karts)")
                .template("MP_ZR_8_Karts02.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("ZSC 32+2 Control Edessa")
                .template("MP_ZSC_m10.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 08 Combined Arms")
                .template("Showcase_Combined_arms.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 04 Firing From Vehicles")
                .template("Showcase_FiringFromVehicles.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 12 Combat Patrol Altis")
                .template("MP_CombatPatrol_01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("COOP 12 Combat Patrol Stratis")
                .template("MP_CombatPatrol_02.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 32 Warlords (Whole Island)")
                .template("MP_Warlords_01.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 64 Warlords (Whole Island)")
                .template("MP_Warlords_01_large.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 48 Warlords (Whole Island)")
                .template("MP_Warlords_01_large_ver2.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 16 Warlords (Central)")
                .template("MP_Warlords_01a.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 16 Warlords (Pyrgos Gulf)")
                .template("MP_Warlords_01b.Altis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 16 Warlords")
                .template("MP_Warlords_02.Stratis")
                .build());
        vanillaMissions.add(Mission.builder()
                .name("SC 32 Warlords")
                .template("MP_Warlords_02_large.Stratis")
                .build());
        return vanillaMissions;
    }

    private List<Mission> maldenMissions()
    {
        final List<Mission> missions = new ArrayList<>();
        missions.add(Mission.builder()
                .name("COOP 12 Combat Patrol")
                .template("MP_CombatPatrol_04.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Escape 10 Malden")
                .template("MP_EscapeFromMalden.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 48+2 Master Malden")
                .template("mp_zgm_m15.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Malden (CSAT)")
                .template("mp_zgm_m15_east.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Malden (NATO)")
                .template("mp_zgm_m15_west.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Malden (AAF)")
                .template("mp_zgm_m15_guer.Malden")
                .build());
        missions.add(Mission.builder()
                .name("Vanguard 30 Chapoi")
                .template("MP_Vanguard_LV_Chapoi.Malden")
                .build());
        missions.add(Mission.builder()
                .name("SC 16 Warlords")
                .template("MP_Warlords_04.Malden")
                .build());
        missions.add(Mission.builder()
                .name("SC 32 Warlords")
                .template("MP_Warlords_04_large.Malden")
                .build());
        return missions;
    }

    private List<Mission> apexMissions()
    {
        final List<Mission> missions = new ArrayList<>();
        missions.add(Mission.builder()
                .name("01 Keystone")
                .template("EXP_m01.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("02 Warm Welcome")
                .template("EXP_m02.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("03 Firestarter")
                .template("EXP_m03.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("04 Heart of Darkness")
                .template("EXP_m04.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("05 Extraction")
                .template("EXP_m05.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("06 Apex Protocol")
                .template("EXP_m06.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("07 End Game")
                .template("EXP_m07.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("End Game 24 Balavu")
                .template("MP_End_Game_04.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("End Game 16 Moddergat")
                .template("MP_End_Game_05.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("ZGM 48+2 Master Tanoa")
                .template("MP_ZGM_m14.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("ZGM 16+2 Master Tanoa (CSAT)")
                .template("MP_ZGM_m14_EAST.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("ZGM 16+2 Master Tanoa (Syndikat)")
                .template("MP_ZGM_m14_GUER.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("ZGM 16+2 Master Tanoa (NATO)")
                .template("MP_ZGM_m14_WEST.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("Escape 10 Tanoa")
                .template("MP_EscapeFromTanoa.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("COOP 12 Combat Patrol")
                .template("MP_CombatPatrol_03.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("Vanguard 50 Power Plant")
                .template("MP_Vanguard_APC_Airport.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("SC 16 Warlords (West)")
                .template("MP_Warlords_03.Tanoa")
                .build());
        missions.add(Mission.builder()
                .name("SC 16 Warlords (South)")
                .template("MP_Warlords_03a.Tanoa")
                .build());
        return missions;
    }

    private List<Mission> contactMissions()
    {
        final List<Mission> missions = new ArrayList<>();
        missions.add(Mission.builder()
                .name("Zeus 48+2 Master Livonia")
                .template("MP_ZGM_m16.Enoch")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Livonia (CSAT)")
                .template("MP_ZGM_m16_east.Enoch")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Livonia (NATO)")
                .template("MP_ZGM_m16_west.Enoch")
                .build());
        missions.add(Mission.builder()
                .name("Zeus 16+2 Master Livonia (LDF)")
                .template("MP_ZGM_m16_guer.Enoch")
                .build());
        missions.add(Mission.builder()
                .name("SC 24 Warlords")
                .template("WarlordsEnoch.Enoch")
                .build());
        missions.add(Mission.builder()
                .name("COOP 12 Combat Patrol")
                .template("combatPatrolEnoch.Enoch")
                .build());
        return missions;
    }
}
