package com.tbag.tbag_backend.domain.Artist.service;

import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Artist.ArtistDto;
import com.tbag.tbag_backend.domain.Artist.ArtistSearchDto;
import com.tbag.tbag_backend.domain.Artist.ArtistMember;
import com.tbag.tbag_backend.domain.Artist.repository.ArtistMemberRepository;
import com.tbag.tbag_backend.domain.Artist.repository.ArtistRepository;
import com.tbag.tbag_backend.domain.Content.ContentArtistRepository;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMemberRepository artistMemberRepository;
    private final ContentArtistRepository contentArtistRepository;


    public Page<ArtistSearchDto> searchArtistsByKeyword(String keyword, Pageable pageable) {
        Page<ArtistMember> artistMembers = artistMemberRepository.searchMembersByKeyword(keyword, pageable);
        return artistMembers.map(this::convertToDTO);
    }

    private ArtistSearchDto convertToDTO(ArtistMember artistMember) {
        ArtistSearchDto artistSearchDto = new ArtistSearchDto();
        Artist artist = artistMember.getArtist();
        ContentArtist contentArtist = contentArtistRepository.findOneByArtist(artist);
        artistSearchDto.setContentId(contentArtist.getContent().getId());
        artistSearchDto.setArtistName(artist.getArtistNameKey());
        artistSearchDto.setProfileImage(artist.getProfileImage());
        artistSearchDto.setMember(new ArtistSearchDto.ArtistMemberDto(artistMember.getId(), artistMember.getArtistMemberNameKey(), artistMember.getProfileImage()));
        artistSearchDto.setViewCount(contentArtist.getContent().getViewCount());
        artistSearchDto.setMediaType(contentArtist.getContent().getMediaType());

        return artistSearchDto;
    }

    @Transactional(readOnly = true)
    public List<ArtistDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(ArtistDto::toArtistDto)
                .collect(Collectors.toList());
    }
}
