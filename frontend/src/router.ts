import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/LoginView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import StudentView from '@/views/student/StudentView.vue';
import AvailableQuizzesView from './views/student/AvailableQuizzesView.vue';
import AvailableTournamentsView from './views/student/AvailableTournamentsView.vue';
import CreatorTournamentsView from './views/student/CreatorTournamentsView.vue';
import SolvedQuizzesView from './views/student/SolvedQuizzesView.vue';
import QuizView from './views/student/quiz/QuizView.vue';
import ResultsView from './views/student/quiz/ResultsView.vue';
import StatsView from './views/student/StatsView.vue';
import ScanView from './views/student/ScanView.vue';
import DashboardView from './views/student/DashboardView.vue';

import AdminManagementView from '@/views/admin/AdminManagementView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateTournamentView from '@/views/student/CreateTournamentView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';
import CoursesView from '@/views/admin/Courses/CoursesView.vue';
import QuestionsStudentsView from '@/views/student/questionsStudents/QuestionsStudentsView.vue';
import ClarificationsTeacherView from '@/views/teacher/clarifications/ClarificationsTeacherView.vue';
import ClarificationsView from '@/views/student/ClarificationsView.vue';
import SubmittedQuestionsView from '@/views/teacher/submittedQuestions/SubmittedQuestionsView.vue';
import PublicClarificationsView from '@/views/student/PublicClarificationsView.vue';
Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: process.env.VUE_APP_NAME, requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Login',
        requiredAuth: 'None'
      }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'submittedQuestions',
          name: 'submitted-questions-management',
          component: SubmittedQuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - SubmittedQuestions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Students',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - ImpExp',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'clarifications',
          name: 'clarifications-management',
          component: ClarificationsTeacherView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Clarifications',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'public/:id',
          name: 'public-clarifications',
          component: PublicClarificationsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Public Clarifications',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'available-tournaments',
          name: 'available-tournaments',
          component: AvailableTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create-tournaments',
          name: 'create-tournaments',
          component: CreateTournamentView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'my-tournaments',
          name: 'my-tournaments',
          component: CreatorTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - My Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quiz',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Stats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Dashboard',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'questions',
          name: 'questions',
          component: QuestionsStudentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Scan',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'clarifications',
          name: 'clarifications',
          component: ClarificationsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Clarifications',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Courses',
            requiredAuth: 'Admin'
          }
        }
      ]
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
