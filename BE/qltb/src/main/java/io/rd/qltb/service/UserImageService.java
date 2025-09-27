package io.rd.qltb.service;


import io.rd.qltb.domain.UserImage;
import io.rd.qltb.model.UserImageDTO;
import io.rd.qltb.repos.UserImageRepository;
import io.rd.qltb.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;

    public UserImageService(final UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    public List<UserImageDTO> findAll() {
        final List<UserImage> userImages = userImageRepository.findAll(Sort.by("id"));
        return userImages.stream()
                .map(userImage -> mapToDTO(userImage, new UserImageDTO()))
                .toList();
    }

    public UserImageDTO get(final Long id) {
        return userImageRepository.findById(id)
                .map(userImage -> mapToDTO(userImage, new UserImageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserImageDTO userImageDTO) {
        final UserImage userImage = new UserImage();
        mapToEntity(userImageDTO, userImage);
        return userImageRepository.save(userImage).getId();
    }

    public void update(final Long id, final UserImageDTO userImageDTO) {
        final UserImage userImage = userImageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userImageDTO, userImage);
        userImageRepository.save(userImage);
    }

    public void delete(final Long id) {
        final UserImage userImage = userImageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        userImageRepository.delete(userImage);
    }

    public UserImageDTO getByUsername(final String username) {
        return userImageRepository.findByUsername(username)
                .map(userImage -> mapToDTO(userImage, new UserImageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    private UserImageDTO mapToDTO(final UserImage userImage, final UserImageDTO userImageDTO) {
        userImageDTO.setId(userImage.getId());
        userImageDTO.setUsername(userImage.getUsername());
        userImageDTO.setImageLink(userImage.getImageLink());
        return userImageDTO;
    }

    private UserImage mapToEntity(final UserImageDTO userImageDTO, final UserImage userImage) {
        userImage.setUsername(userImageDTO.getUsername());
        userImage.setImageLink(userImageDTO.getImageLink());
        return userImage;
    }

}
