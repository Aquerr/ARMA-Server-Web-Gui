import {Component, inject, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {DiscordSettingsFormService} from "./discord-settings-form.service";

@Component({
  selector: 'app-settings-discord',
  templateUrl: './settings-discord.component.html',
  styleUrl: './settings-discord.component.scss',
  standalone: false
})
export class SettingsDiscordComponent implements OnInit {

  public form!: FormGroup;

  private readonly formService: DiscordSettingsFormService = inject(DiscordSettingsFormService);

  ngOnInit(): void {
    this.form = this.formService.getForm();
  }
}
