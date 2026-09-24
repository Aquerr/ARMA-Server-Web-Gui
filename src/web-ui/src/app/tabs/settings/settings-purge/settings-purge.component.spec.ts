import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPurgeComponent } from './settings-purge.component';

describe('SettingsPurgeComponent', () => {
  let component: SettingsPurgeComponent;
  let fixture: ComponentFixture<SettingsPurgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsPurgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPurgeComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
