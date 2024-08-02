<template>
  <div class="search-wrap">
    <div class="search">
      <input
        type="text"
        class="searchTerm"
        placeholder="Which team are you looking for?"
        v-model="searchQuery"
        @input="handleInput"
        @keyup.enter="searchTeams"
      />
      <button type="submit" class="searchButton" @click="searchTeams">
        <font-awesome-icon icon="search" />
      </button>
    </div>
    <ul v-if="showTeams && filteredTeams.length" class="results">
      <li v-for="team in filteredTeams" :key="team.id" @click="searchTeam(team)">
        {{ team.teamName }}
      </li>
    </ul>
  </div>
  <div class="results-wrap" v-if="showResults">
    <TeamSearchResult v-for="team in displayedTeams" :key="team.id" :team="team" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import TeamSearchResult from '@/components/TeamSearchResult.vue';

const teamStore = useTeamStore();
const searchQuery = ref('');
const showTeams = ref(false);
const showResults = ref(false);
const selectedTeam = ref(null);
const router = useRouter();

const filteredTeams = computed(() =>
  teamStore.teams.filter(team => team.teamName.toLowerCase().includes(searchQuery.value.toLowerCase()))
);

const displayedTeams = computed(() => {
  if (selectedTeam.value) {
    return [selectedTeam.value];
  } else {
    return filteredTeams.value;
  }
});

const handleInput = () => {
  showTeams.value = searchQuery.value.length > 0;
};

const searchTeams = () => {
  if (filteredTeams.value.length) {
    selectedTeam.value = null;
    showResults.value = true;
    showTeams.value = false
  } else {
    showResults.value = false;
  }
};

const searchTeam = (team) => {
  selectedTeam.value = team;
  searchQuery.value = team.name
  showTeams.value = false;
  showResults.value = true;
};
</script>

<style scoped>
body {
  background: #f2f2f2;
  font-family: 'Open Sans', sans-serif;
}

.search-wrap {
  width: 50%;
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.search {
  width: 100%;
  position: relative;
  display: flex;
}

.searchTerm {
  width: 100%;
  border: 3px solid #e1bee7;
  border-right: none;
  padding: 5px;
  height: 36px; /* 높이를 버튼과 맞춤 */
  border-radius: 5px 0 0 5px;
  outline: none;
  color: #9dbfaf;
  transition: all 0.3s ease; /* 부드러운 전환 효과 */
}

.searchTerm:focus {
  color: #e1bee7;
  width: 200%; /* 입력창 확장 */
}

.searchButton {
  width: 40px;
  height: 36px; /* input 창과 동일한 높이로 설정 */
  border: 3px solid #e1bee7; /* 동일한 border width 적용 */
  border-left: none; /* input 창과 매끄럽게 연결 */
  background: #f8bbd0;
  text-align: center;
  color: #fff;
  border-radius: 0 5px 5px 0; /* input 창과 맞춤 */
  cursor: pointer;
  font-size: 20px;
}

.searchButton:hover {
  background: #e1bee7;
}

.results {
  list-style-type: none;
  padding: 0;
  margin: 0;
  position: absolute;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #e1bee7;
  border-radius: 0 0 5px 5px;
  background: #fff;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  top: 100%;
}

.results li {
  padding: 10px;
  cursor: pointer;
}

.results li:hover {
  background: #e1bee7;
}

.results-wrap {
  width: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, 0);
  padding-top: 20px;
}
</style>
