package nextstep.subway.member.application;

import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest request) {
        Member member = memberRepository.save(request.toMember());
        return MemberResponse.of(member);
    }

    public MemberResponse findMember(LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.getId()).orElseThrow(MemberNotFoundException::new);
        return MemberResponse.of(member);
    }

    public void updateMember(LoginMember loginMember, MemberRequest param) {
        Member member = memberRepository.findById(loginMember.getId()).orElseThrow(MemberNotFoundException::new);
        member.update(param.toMember());
    }

    public void deleteMember(LoginMember loginMember) {
        try {
            memberRepository.deleteById(loginMember.getId());
        } catch (IllegalArgumentException e) {
            throw new MemberNotFoundException();
        }
    }

    public void checkValidation(String email) {
        memberRepository.findByEmail(email).ifPresent(it -> {
            throw new AlreadyExistEmailException();
        });
    }
}