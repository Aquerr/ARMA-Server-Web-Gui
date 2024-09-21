export interface DifficultyProfile {
  id: number;
  name: string;
  active: boolean;
  options: DifficultyOptions;
}

export interface DifficultyOptions {
  reducedDamage: boolean;

  groupIndicators: number;
  friendlyTags: number;
  enemyTags: number;
  detectedMines: number;
  commands: number;
  waypoints: number;
  tacticalPing: number;

  weaponInfo: number;
  stanceIndicator: number;
  staminaBar: boolean;
  weaponCrosshair: boolean;
  visionAid: boolean;

  thirdPersonView: number;
  cameraShake: boolean;

  scoreTable: boolean;
  deathMessages: boolean;
  vonId: boolean;

  mapContentFriendly: boolean;
  mapContentEnemy: boolean;
  mapContentMines: boolean;

  autoReport: boolean;
  multipleSaves: boolean;

  aiLevelPreset: number;

  skillAI: string;
  precisionAI: string;
}
