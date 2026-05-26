import { Routes } from '@angular/router';
import { AuthPage } from './features/auth/auth-page/auth-page';
import { ChangePasswordPage } from './features/auth/change-password-page/change-password-page';
import { ProfilePage } from './features/users/users-page/profile-page';
import { SavedToursListComponent } from './features/users/users-action-page/saved-page/favorite-page';
import { TouristPlaces } from './features/tourist-places/tourist-places';
import { TouristPlace } from './features/tourist-place/tourist-place';
import { Home } from './features/home/home';
import { CreateTouristPlace } from './features/admin/create-tourist-place/create-tourist-place';
import { AdminTouristPlaces } from './features/admin/admin-tourist-places/admin-tourist-places';
import { EditTouristPlace } from './features/admin/edit-tourist-place/edit-tourist-place';
import { adminGuard } from './core/guards/admin.guard';
import { adminPlacesRedirectGuard } from './core/guards/admin-places-redirect.guard';
import { TourListComponent } from './features/tours/tour-list/tour-list';
import { TourDetailComponent } from './features/tours/tour-detail/tour-detail';
import {CreateTour} from './features/admin/create-tours/create-tour';

export const routes: Routes = [
    { path: '', component: Home, pathMatch: 'full' },
    { path: 'admin/lugares', component: AdminTouristPlaces, canActivate: [adminGuard] },
    { path: 'admin/lugares/crear', component: CreateTouristPlace, canActivate: [adminGuard] },
    { path: 'admin/lugares/:id/editar', component: EditTouristPlace, canActivate: [adminGuard] },
    { path: 'admin/recorridos/crear', component: CreateTour, canActivate: [adminGuard] },
    { path: 'admin/recorridos/:id/editar', component: EditTouristPlace, canActivate: [adminGuard] },
    { path: 'lugares/:id', component: TouristPlace },
    { path: 'lugares', component: TouristPlaces, canActivate: [adminPlacesRedirectGuard] },
    { path: 'profile', component: ProfilePage },
    { path: 'recorridos-guardados', component: SavedToursListComponent},
    { path: 'recorridos', component: TourListComponent },
    { path: 'recorridos/:id', component: TourDetailComponent },
    { path: 'tour/:id', component: TourDetailComponent },
    { path: 'auth/change-password', component: ChangePasswordPage },
    { path: 'auth', component: AuthPage, pathMatch: 'full' }
];
