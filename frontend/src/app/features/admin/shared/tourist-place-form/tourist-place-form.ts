import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TouristPlaceEnvironment } from '../../../tourist-places/tourist-places.types';

export interface TouristPlaceCategoryOption {
  id: number;
  name: string;
}

export interface TouristPlacePhotoInput {
  filePath: string;
  description: string;
}

@Component({
  selector: 'app-tourist-place-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './tourist-place-form.html',
  styleUrl: './tourist-place-form.css',
})
export class TouristPlaceForm {
  @Input() name = '';
  @Output() nameChange = new EventEmitter<string>();

  @Input() description = '';
  @Output() descriptionChange = new EventEmitter<string>();

  @Input() duration = '';
  @Output() durationChange = new EventEmitter<string>();

  @Input() environment: TouristPlaceEnvironment = 'EXTERIOR';
  @Output() environmentChange = new EventEmitter<TouristPlaceEnvironment>();

  @Input() country = '';
  @Output() countryChange = new EventEmitter<string>();

  @Input() department = '';
  @Output() departmentChange = new EventEmitter<string>();

  @Input() city = '';
  @Output() cityChange = new EventEmitter<string>();

  @Input() latitude: number | null = null;
  @Output() latitudeChange = new EventEmitter<number | null>();

  @Input() longitude: number | null = null;
  @Output() longitudeChange = new EventEmitter<number | null>();

  @Input() newCategoryName = '';
  @Output() newCategoryNameChange = new EventEmitter<string>();

  @Input() countries: string[] = [];
  @Input() departments: string[] = [];
  @Input() cities: string[] = [];
  @Input() loadingCountries = false;
  @Input() loadingDepartments = false;
  @Input() loadingCities = false;

  @Input() categories: TouristPlaceCategoryOption[] = [];
  @Input() selectedCategoryIds: number[] = [];
  @Input() newCategories: string[] = [];
  @Input() loadingCategories = false;

  @Input() activities: string[] = [''];
  @Input() photos: TouristPlacePhotoInput[] = [{ filePath: '', description: '' }];

  @Input() saving = false;
  @Input() message = '';
  @Input() error = '';
  @Input() submitLabel = 'Guardar';
  @Input() savingLabel = 'Guardando...';

  @Output() countrySelected = new EventEmitter<string>();
  @Output() departmentSelected = new EventEmitter<string>();
  @Output() categoryToggled = new EventEmitter<{ categoryId: number; checked: boolean }>();
  @Output() newCategoryAdded = new EventEmitter<void>();
  @Output() newCategoryRemoved = new EventEmitter<string>();
  @Output() activityAdded = new EventEmitter<void>();
  @Output() activityUpdated = new EventEmitter<{ index: number; value: string }>();
  @Output() activityRemoved = new EventEmitter<number>();
  @Output() photoAdded = new EventEmitter<void>();
  @Output() photoUpdated = new EventEmitter<{
    index: number;
    field: keyof TouristPlacePhotoInput;
    value: string;
  }>();
  @Output() photoRemoved = new EventEmitter<number>();
  @Output() formSubmitted = new EventEmitter<void>();

  readonly environments: { value: TouristPlaceEnvironment; label: string }[] = [
    { value: 'EXTERIOR', label: 'Exterior' },
    { value: 'INTERIOR', label: 'Interior' },
    { value: 'MIXED', label: 'Mixto' },
  ];

  readonly trackByIndex = (index: number): number => index;

  onCountryChange(value: string): void {
    this.countryChange.emit(value);
    this.countrySelected.emit(value);
  }

  onDepartmentChange(value: string): void {
    this.departmentChange.emit(value);
    this.departmentSelected.emit(value);
  }

  onLatitudeChange(value: number | null): void {
    this.latitudeChange.emit(value === undefined ? null : value);
  }

  onLongitudeChange(value: number | null): void {
    this.longitudeChange.emit(value === undefined ? null : value);
  }
}
