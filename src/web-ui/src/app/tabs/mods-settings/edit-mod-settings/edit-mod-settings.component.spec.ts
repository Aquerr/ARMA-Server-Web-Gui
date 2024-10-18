import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditModSettingsComponent } from './edit-mod-settings.component';

describe('EditModSettingsComponent', () => {
  let component: EditModSettingsComponent;
  let fixture: ComponentFixture<EditModSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditModSettingsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditModSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
