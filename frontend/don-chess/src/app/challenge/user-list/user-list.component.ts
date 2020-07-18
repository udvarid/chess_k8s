import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserDto } from 'src/app/shared/dto/userDto.model';
import { Subscription } from 'rxjs';
import { ChallengeService } from '../challenge.service';
import { ChallengeCreateDto } from 'src/app/shared/dto/challengeCreateDto.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, OnDestroy {

  public users: UserDto[];
  private userChangedSubs: Subscription;
  private userListRefreshedSubs: Subscription;
  public isLoading = false;

  constructor(private challengeService: ChallengeService) { }

  ngOnInit() {
    this.userChangedSubs = this.challengeService.usersChanged
    .subscribe(
      (users: UserDto[]) => {
        this.users = users;
        this.isLoading = false;
      }
    );
    this.userListRefreshedSubs = this.challengeService.userListRefreshed
    .subscribe(
      (response: boolean) => {
        this.isLoading = true;
      }
    );

    this.isLoading = true;
    this.challengeService.getUserDetail();
  }

  ngOnDestroy() {
    this.userChangedSubs.unsubscribe();
    this.userListRefreshedSubs.unsubscribe();
  }

  onChallenge(index: number) {
    const challenge: ChallengeCreateDto = {challengedId: this.users[index].id};
    this.challengeService.challengeUser(challenge);
  }
}
