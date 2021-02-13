package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/tournaments")
    public List<TournamentDto> getTournaments() {
        return tournamentService.findAllOpen();
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/tournaments/{tournamentId}")
    public List<TournamentDto> getTournamentById(@PathVariable Integer tournamentId) {
        return tournamentService.findById(tournamentId);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/tournaments/{tournamentId}/studentCount")
    public int getEnrolledStudentsCount(@PathVariable Integer tournamentId) {
        return tournamentService.getEnrolledStudentsCount(tournamentId);
    }

    @PostMapping("/tournaments/created")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getTournamentsByCreator(@RequestBody Integer CreatorId) {
        return tournamentService.findByCreatorId(CreatorId);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/tournaments")
    public TournamentDto createTournament(@RequestBody TournamentDto TournamentDto) {
        return tournamentService.createTournament(TournamentDto);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/tournaments/{tournamentId}/enroll")
    public TournamentDto getTournamentStudents(@PathVariable Integer tournamentId, @RequestBody String username) {
        return tournamentService.enrollStudent(tournamentId, username);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    @GetMapping("/tournaments/{executionId}/{username}/enrollable")
    public List<TournamentDto> getEnrollableTournaments(@PathVariable Integer executionId, @PathVariable String username) {
        return tournamentService.findAllEnrollable(username, executionId);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/tournaments/cancel")
    public void cancelTournament(@RequestBody Integer tournamentId) {
        tournamentService.cancelTournament(tournamentId);
        return;
    }

}