import { Component, OnInit } from "@angular/core";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { NotificationService } from "../../service/notification.service";
import { ServerDifficultyService } from "../../service/server-difficulty.service";
import { DifficultyOptions, DifficultyProfile } from "../../model/difficulty-profile.model";

@Component({
  selector: "app-difficulty",
  templateUrl: "./difficulty.component.html",
  styleUrl: "./difficulty.component.scss",
  standalone: false
})
export class DifficultyComponent implements OnInit {
  difficultyProfiles: DifficultyProfile[] = [];

  constructor(
    private maskService: LoadingSpinnerMaskService,
    private difficultyService: ServerDifficultyService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.reloadProfiles();
  }

  addNewDifficulty() {
    let newDifficulty = this.prepareNewDifficultyProfile();
    this.difficultyProfiles.push(newDifficulty);
  }

  saveAll() {
    this.maskService.show();
    this.difficultyService.saveDifficulties(this.difficultyProfiles).subscribe((response) => {
      this.maskService.hide();
      this.notificationService.successNotification("Difficulty profiles has been saved!");
      this.reloadProfiles();
    });
  }

  private reloadProfiles() {
    this.maskService.show();
    this.difficultyService.getDifficulties().subscribe((response) => {
      this.difficultyProfiles = response;
      this.maskService.hide();
    });
  }

  prepareNewDifficultyProfile() {
    let options = {
      reducedDamage: false,

      groupIndicators: 0,
      friendlyTags: 0,
      enemyTags: 0,
      detectedMines: 0,
      commands: 0,
      waypoints: 0,
      tacticalPing: 0,

      weaponInfo: 0,
      stanceIndicator: 0,
      staminaBar: false,
      weaponCrosshair: false,
      visionAid: false,

      thirdPersonView: 0,
      cameraShake: false,

      scoreTable: false,
      deathMessages: false,
      vonId: false,

      mapContentFriendly: false,
      mapContentEnemy: false,
      mapContentMines: false,

      autoReport: false,
      multipleSaves: false,

      aiLevelPreset: 0,

      skillAI: "0.5",
      precisionAI: "0.5"
    } as DifficultyOptions;

    return { name: "new", active: false, options: options } as DifficultyProfile;
  }

  onProfileActivate(difficultyProfile: DifficultyProfile) {
    if (!difficultyProfile.active) {
      this.difficultyProfiles = this.difficultyProfiles.map((profile) => {
        profile.active = false;
        return profile;
      });
    }
    difficultyProfile.active = !difficultyProfile.active;
  }

  onProfileSave(difficultyProfile: DifficultyProfile) {
    this.reloadProfiles();
  }

  onProfileDelete(difficultyProfile: DifficultyProfile) {
    this.reloadProfiles();
  }
}
