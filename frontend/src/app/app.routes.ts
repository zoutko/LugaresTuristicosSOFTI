import { Routes } from '@angular/router';
import { AuthPage } from './features/auth/auth-page/auth-page';
import { ChangePasswordPage } from './features/auth/change-password-page/change-password-page';
import { ProfilePage } from './features/users/users-page/profile-page';
import { TouristPlaces } from './features/tourist-places/tourist-places';
import { TouristPlace } from './features/tourist-place/tourist-place';
import { Home } from './features/home/home';

export const routes: Routes = [
    { path: '', component: Home, pathMatch: 'full' },
    { path: 'lugares/:id', component: TouristPlace },
    { path: 'lugares', component: TouristPlaces },
    { path: 'profile', component: ProfilePage },
    { path: 'auth/change-password', component: ChangePasswordPage },
    { path: 'auth', component: AuthPage, pathMatch: 'full' }
];
